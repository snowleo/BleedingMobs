/*
 * GoreMod - a blood plugin for Bukkit
 * Copyright (C) 2011  snowleo
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.snowleo.goremod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.bukkit.plugin.java.JavaPlugin;


public class GoreMod extends JavaPlugin implements IGoreMod
{
	private final static int MAX_PARTICLES = 200;
	private transient ParticleStorage storage;
	private transient Set<String> worlds = Collections.emptySet();

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
		pluginManager.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Low, this);
		pluginManager.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Low, this);
		pluginManager.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
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
		config.options().header("Gore Mod config\n"
								+ "Don't use tabs in this file\n"
								+ "Be careful, if you change anything, it can break your server.\n"
								+ "For example creating thousands of particles on hit is not a good idea.\n"
								+ "You have been warned!\n"
								+ "You can always reset this to the defaults by removing the file.\n");
		
		final int maxParticles = Math.max(20, config.getInt("max-particles", MAX_PARTICLES));
		config.set("max-particles", maxParticles);
		for (ParticleType particleType : ParticleType.values())
		{
			final String name = particleType.toString().toLowerCase();
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
				converted.add(material.toString().toLowerCase().replaceAll("_", "-"));
			}
			config.set(name + ".saturated-materials", converted);
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
}
