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
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;


public class Settings
{
	private final static int MAX_PARTICLES = 200;
	private transient Set<String> worlds = Collections.emptySet();
	private transient boolean bleedWhenCanceled = false;
	private transient boolean bleedingEnabled = true;
	private transient int maxParticles = 200;
	private transient final IBleedingMobs plugin;
	
	public Settings(final IBleedingMobs plugin)
	{
		this.plugin = plugin;
		loadConfig();
		saveConfig();
	}
	
	public final void loadConfig()
	{
		final FileConfiguration config = plugin.getConfig();
		bleedingEnabled = config.getBoolean("enabled", true);
		final int newMaxParticles = Math.max(20, config.getInt("max-particles", MAX_PARTICLES));
		if (plugin.getStorage() != null)
		{
			plugin.getStorage().changeMaxParticles(newMaxParticles - maxParticles);
		}
		maxParticles = newMaxParticles;
		bleedWhenCanceled = config.getBoolean("bleed-when-canceled", false);
		for (ParticleType particleType : ParticleType.values())
		{
			final String name = particleType.toString().toLowerCase(Locale.ENGLISH);
			particleType.setWoolChance(Math.min(100, Math.max(0, config.getInt(name + ".wool-chance", particleType.getWoolChance()))));
			particleType.setBoneChance(Math.min(100, Math.max(0, config.getInt(name + ".bone-chance", particleType.getBoneChance()))));
			particleType.setParticleLifeFrom(Math.max(0, config.getInt(name + ".particle-life.from", particleType.getParticleLifeFrom())));
			particleType.setParticleLifeTo(Math.max(particleType.getParticleLifeFrom(), config.getInt(name + ".particle-life.to", particleType.getParticleLifeTo())));
			final String colorName = config.getString(name + ".wool-color", particleType.getWoolColor().toString()).replaceAll("[_-]", "").toUpperCase(Locale.ENGLISH);
			byte woolcolor = -1;
			for (DyeColor dyeColor : DyeColor.values())
			{
				if (dyeColor.toString().replace("_", "").equals(colorName))
				{
					woolcolor = dyeColor.getData();
				}
			}
			if (woolcolor < 0)
			{
				woolcolor = ((Number)Math.min(15, Math.max(0, config.getInt(name + ".wool-color", particleType.getWoolColor().getData())))).byteValue();
			}
			particleType.setWoolColor(DyeColor.getByData(woolcolor));
			particleType.setStainsFloor(config.getBoolean(name + ".stains-floor", particleType.isStainingFloor()));
			particleType.setBoneLife(Math.max(0, config.getInt(name + ".bone-life", particleType.getBoneLife())));
			particleType.setStainLifeFrom(Math.max(0, config.getInt(name + ".stain-life.from", particleType.getStainLifeFrom())));
			particleType.setStainLifeTo(Math.max(particleType.getStainLifeFrom(), config.getInt(name + ".stain-life.to", particleType.getStainLifeTo())));
			particleType.setAmountFrom(Math.max(0, config.getInt(name + ".amount.from", particleType.getAmountFrom())));
			particleType.setAmountTo(Math.max(particleType.getAmountFrom(), config.getInt(name + ".amount.to", particleType.getAmountTo())));
			final List<String> mats = config.getStringList(name + ".saturated-materials");
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
			final String particleMatName = config.getString(name + ".particle-material");
			if (particleMatName != null)
			{
				final Material material = Material.matchMaterial(particleMatName.replaceAll("-", "_"));
				if (material != null)
				{
					particleType.setParticleMaterial(material);
				}
			}
		}
		final List<String> coll = config.getStringList("worlds");
		worlds = new HashSet<String>(coll == null ? Collections.<String>emptyList() : coll);
	}
	
	public final void saveConfig()
	{
		final FileConfiguration config = plugin.getConfig();
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
								+ "Wool colors: white, orange, magenta, light-blue, yellow, lime, pink,\n"
								+ "gray, silver, cyan, purple, blue, brown, green, red, black\n");
		
		config.set("enabled", bleedingEnabled);
		config.set("max-particles", maxParticles);
		config.set("bleed-when-canceled", bleedWhenCanceled);
		for (ParticleType particleType : ParticleType.values())
		{
			final String name = particleType.toString().toLowerCase(Locale.ENGLISH);
			config.set(name + ".wool-chance", particleType.getWoolChance());
			config.set(name + ".bone-chance", particleType.getBoneChance());
			config.set(name + ".particle-life.from", particleType.getParticleLifeFrom());
			config.set(name + ".particle-life.to", particleType.getParticleLifeTo());
			config.set(name + ".wool-color", particleType.getWoolColor().toString().replace("_", "-").toLowerCase(Locale.ENGLISH));
			config.set(name + ".stains-floor", particleType.isStainingFloor());
			config.set(name + ".bone-life", particleType.getBoneLife());
			config.set(name + ".stain-life.from", particleType.getStainLifeFrom());
			config.set(name + ".stain-life.to", particleType.getStainLifeTo());
			config.set(name + ".amount.from", particleType.getAmountFrom());
			config.set(name + ".amount.to", particleType.getAmountTo());
			final List<String> converted = new ArrayList<String>();
			for (Material material : particleType.getSaturatedMaterials())
			{
				converted.add(material.toString().toLowerCase(Locale.ENGLISH).replaceAll("_", "-"));
			}
			config.set(name + ".saturated-materials", converted);
			config.set(name + ".particle-material", particleType.getParticleMaterial().toString().toLowerCase(Locale.ENGLISH).replaceAll("_", "-"));
		}
		config.set("worlds", new ArrayList<String>(worlds));
		plugin.saveConfig();
	}
	
	public boolean isWorldEnabled(final World world)
	{
		return worlds.isEmpty() || worlds.contains(world.getName());
	}
	
	public int getMaxParticles()
	{
		return maxParticles;
	}
	
	public boolean isBleedingWhenCanceled()
	{
		return bleedWhenCanceled;
	}
	
	public boolean isBleedingEnabled()
	{
		return bleedingEnabled;
	}
	
	public void setBleedingEnabled(final boolean set)
	{
		this.bleedingEnabled = set;
	}
	
	public Set<String> getWorlds()
	{
		return worlds;
	}
	
	public void setMaxParticles(final int maxParticles)
	{
		this.maxParticles = maxParticles;
	}

	public void setBleedWhenCanceled(final boolean set)
	{
		this.bleedWhenCanceled = set;
	}
}
