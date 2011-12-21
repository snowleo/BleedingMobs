/*
 * BleedingMobs - make your monsters and players bleed
 *
 * Copyright (C) 2011 snowleo
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.snowleo.bleedingmobs;

import java.util.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.world.WorldListener;
import org.bukkit.plugin.PluginManager;


public class BleedingMobs extends me.Perdog.BleedingMobs.BleedingMobs implements IBleedingMobs
{
	private final static int MAX_PARTICLES = 200;
	private transient ParticleStorage storage;
	private transient Set<String> worlds = Collections.emptySet();
	private transient boolean spawning = false;
	private transient boolean bleedWhenCanceled = false;

	@Override
	public void onDisable()
	{
		if (storage != null)
		{
			storage.clearAllParticles();
		}
	}

	@Override
	public void onEnable()
	{
		final int maxParticles = loadConfig();
		storage = new ParticleStorage(this, maxParticles);

		registerListeners();

		final String loadMessage = getDescription().getFullName() + " loaded. Have fun!";
		getServer().getLogger().info(loadMessage);
	}

	private void registerListeners()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		final EntityListener entityListener = new ParticleEntityListener(this);
		pluginManager.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Monitor, this);
		pluginManager.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Low, this);
		pluginManager.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
		pluginManager.registerEvent(Type.ENDERMAN_PICKUP, entityListener, Priority.Low, this);
		final PlayerListener playerListener = new ParticlePlayerListener(this);
		pluginManager.registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Low, this);
		final BlockListener blockListener = new ParticleBlockListener(this);
		pluginManager.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Low, this);
		pluginManager.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Low, this);
		pluginManager.registerEvent(Type.BLOCK_IGNITE, blockListener, Priority.Low, this);
		final WorldListener worldListener = new ParticleWorldListener(this);
		pluginManager.registerEvent(Type.CHUNK_UNLOAD, worldListener, Priority.Low, this);

		pluginManager.registerEvent(Type.BLOCK_PISTON_EXTEND, blockListener, Priority.Low, this);
		pluginManager.registerEvent(Type.BLOCK_PISTON_RETRACT, blockListener, Priority.Low, this);

	}

	private int loadConfig()
	{
		final FileConfiguration config = this.getConfig();
		config.options().header("Bleeding Mobs config\n"
								+ "Don't use tabs in this file\n"
								+ "Be careful, if you change the amounts of particles, it can break your server.\n"
								+ "For example creating thousands of particles on hit is not a good idea.\n"
								+ "You can always reset this to the defaults by removing the file.\n"
								+ "Chances are from 0 to 100, no fractions allowed. 100 means 100% chance of drop.\n"
								+ "There is no chance value for the particle material (e.g. redstone), \n"
								+ "because it's calculated from the wool and bone chances (so if you set them both to 0, it's 100%).\n"
								+ "All time values are in ticks = 1/20th of a second.\n"
								+ "If there are from and to values, then the value is randomly selected between from and to.\n"
								+ "Wool colors: 0 white; 1 orange; 2 magenta; 3 light blue; 4 yellow; 5 lime; 6 pink;\n"
								+ "7 gray; 8 light gray; 9 cyan; 10 purple; 11 blue; 12 brown; 13 green; 14 red; 15 black\n");

		final int maxParticles = Math.max(20, config.getInt("max-particles", MAX_PARTICLES));
		config.set("max-particles", maxParticles);
		bleedWhenCanceled = config.getBoolean("bleed-when-canceled", false);
		config.set("bleed-when-canceled", bleedWhenCanceled);
		for (ParticleType particleType : ParticleType.values())
		{
			final String name = particleType.toString().toLowerCase(Locale.ENGLISH);
			particleType.setWoolChance(Math.min(100, Math.max(0, config.getInt(name + ".wool-chance", particleType.getWoolChance()))));
			config.set(name + ".wool-chance", particleType.getWoolChance());
			particleType.setBoneChance(Math.min(100, Math.max(0, config.getInt(name + ".bone-chance", particleType.getBoneChance()))));
			config.set(name + ".bone-chance", particleType.getBoneChance());
			particleType.setParticleLifeFrom(Math.max(0, config.getInt(name + ".particle-life.from", particleType.getParticleLifeFrom())));
			config.set(name + ".particle-life.from", particleType.getParticleLifeFrom());
			particleType.setParticleLifeTo(Math.max(particleType.getParticleLifeFrom(), config.getInt(name + ".particle-life.to", particleType.getParticleLifeTo())));
			config.set(name + ".particle-life.to", particleType.getParticleLifeTo());
			particleType.setWoolColor(Math.min(15, Math.max(0, config.getInt(name + ".wool-color", particleType.getWoolColor()))));
			config.set(name + ".wool-color", particleType.getWoolColor());
			particleType.setStainsFloor(config.getBoolean(name + ".stains-floor", particleType.isStainingFloor()));
			config.set(name + ".stains-floor", particleType.isStainingFloor());
			particleType.setBoneLife(Math.max(0, config.getInt(name + ".bone-life", particleType.getBoneLife())));
			config.set(name + ".bone-life", particleType.getBoneLife());
			particleType.setStainLifeFrom(Math.max(0, config.getInt(name + ".stain-life.from", particleType.getStainLifeFrom())));
			config.set(name + ".stain-life.from", particleType.getStainLifeFrom());
			particleType.setStainLifeTo(Math.max(particleType.getStainLifeFrom(), config.getInt(name + ".stain-life.to", particleType.getStainLifeTo())));
			config.set(name + ".stain-life.to", particleType.getStainLifeTo());
			particleType.setAmountFrom(Math.max(0, config.getInt(name + ".amount.from", particleType.getAmountFrom())));
			config.set(name + ".amount.from", particleType.getAmountFrom());
			particleType.setAmountTo(Math.max(particleType.getAmountFrom(), config.getInt(name + ".amount.to", particleType.getAmountTo())));
			config.set(name + ".amount.to", particleType.getAmountTo());
			final List<String> mats = config.getList(name + ".saturated-materials", null);
			final EnumSet<Material> materials = EnumSet.noneOf(Material.class);
			if (mats != null)
			{
				for (String matName : mats)
				{
					final Material material = Material.matchMaterial(matName.replaceAll("-", "_"));
					if (material != null)
					{
						materials.add(material);
					}
				}
			}
			if (!materials.isEmpty())
			{
				particleType.setSaturatedMaterials(materials);
			}
			final List<String> converted = new ArrayList<String>();
			for (Material material : particleType.getSaturatedMaterials())
			{
				converted.add(material.toString().toLowerCase(Locale.ENGLISH).replaceAll("_", "-"));
			}
			config.set(name + ".saturated-materials", converted);
			final String particleMatName = config.getString(name + ".particle-material");
			if (particleMatName != null)
			{
				final Material material = Material.matchMaterial(particleMatName.replaceAll("-", "_"));
				if (material != null)
				{
					particleType.setParticleMaterial(material);
				}
			}
			config.set(name + ".particle-material", particleType.getParticleMaterial().toString().toLowerCase(Locale.ENGLISH).replaceAll("_", "-"));
		}
		final Collection coll = config.getList("worlds", null);
		worlds = new HashSet(coll == null ? Collections.emptyList() : coll);
		config.set("worlds", new ArrayList(worlds));
		this.saveConfig();
		return maxParticles;
	}

	@Override
	public ParticleStorage getStorage()
	{
		return storage;
	}

	@Override
	public boolean isWorldEnabled(final World world)
	{
		return worlds.isEmpty() || worlds.contains(world.getName());
	}

	@Override
	public boolean isSpawning()
	{
		return spawning;
	}

	@Override
	public void setSpawning(final boolean spawning)
	{
		this.spawning = spawning;
	}

	@Override
	public boolean isBleedingWhenCanceled()
	{
		return bleedWhenCanceled;
	}

	/**
	 * @deprecated Use getStorage().isParticleItem(id) instead.
	 */
	@Override
	@Deprecated
	public boolean isParticleItem(final UUID uuid)
	{
		return getStorage().isParticleItem(uuid);
	}
}
