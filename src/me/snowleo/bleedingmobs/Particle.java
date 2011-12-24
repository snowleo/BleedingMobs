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

import java.util.EnumSet;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;


public class Particle implements Runnable
{
	private transient Material mat;
	private transient Item item;
	private final transient BukkitScheduler scheduler;
	private final transient IBleedingMobs plugin;
	private final transient Random random = new Random();
	private transient ParticleType type = ParticleType.ATTACK;
	private transient boolean meltedSnow;


	enum State
	{
		UNKNOWN,
		SPAWNED,
		FLOWING
	}
	private transient State state = State.UNKNOWN;
	private transient Material savedBlockMat;
	private transient Location savedBlockLoc;
	private transient EnumSet<Material> saturatedMats = EnumSet.noneOf(Material.class);
	private transient byte savedBlockData;

	public Particle(final IBleedingMobs plugin)
	{
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
	}

	public void start(final Location loc, final ParticleType type)
	{
		this.type = type;
		this.saturatedMats = type.getSaturatedMaterials();
		final int rand = random.nextInt(100);
		final int span = type.getParticleLifeTo() - type.getParticleLifeFrom();
		int lifetime = (span > 0 ? random.nextInt(span) : 0) + type.getParticleLifeFrom();
		ItemStack stack;
		if (rand < type.getWoolChance())
		{
			mat = Material.WOOL;
			if (type.getParticleMaterial() == Material.CAKE)
			{
				stack = new ItemStack(mat, 1, (short)getRandomColor());
			}
			else
			{
				stack = new ItemStack(mat, 1, (short)type.getWoolColor());
			}
		}
		else if (rand > (99 - type.getBoneChance()))
		{
			mat = Material.BONE;
			stack = new ItemStack(mat, 1);
			lifetime += type.getBoneLife();
		}
		else
		{
			mat = type.getParticleMaterial();
			stack = new ItemStack(mat, 1);
		}
		plugin.setSpawning(true);
		item = loc.getWorld().dropItemNaturally(loc, stack);
		plugin.setSpawning(false);
		plugin.getStorage().addParticleItem(((CraftItem)item).getUniqueId(), this);
		state = State.SPAWNED;
		scheduler.scheduleSyncDelayedTask(plugin, this, lifetime);
	}

	@Override
	public void run()
	{
		if (state == State.SPAWNED)
		{
			if (mat == Material.BONE)
			{
				item.remove();
				plugin.getStorage().removeParticleItem(((CraftItem)item).getUniqueId());
				plugin.getStorage().freeParticle(this);
				return;
			}
			if (mat == type.getParticleMaterial() || mat == Material.WOOL)
			{
				Block block = item.getLocation().getBlock();
				if (block == null || block.getType() == Material.AIR || block.getType() == Material.SNOW)
				{
					block = item.getLocation().getBlock().getRelative(BlockFace.DOWN);
				}
				if (block != null && type.isStainingFloor() && saturatedMats.contains(block.getType()) && !plugin.getStorage().isUnbreakable(block.getLocation()))
				{
					if (type.getParticleMaterial() == Material.CAKE)
					{
						stainFloor(block, getRandomColor());
					}
					else
					{
						stainFloor(block, type.getWoolColor());
					}
				}
				else
				{
					plugin.getStorage().freeParticle(this);
				}
				item.remove();
				plugin.getStorage().removeParticleItem(((CraftItem)item).getUniqueId());
				return;
			}
			Bukkit.getLogger().log(Level.SEVERE, "Invalid particle state! Material: {0}", mat.toString());
		}
		if (state == State.FLOWING)
		{
			restoreBlock(true);
		}
		else
		{
			plugin.getStorage().freeParticle(this);
		}
	}

	private int getRandomColor()
	{
		// 1 2 3 4 5 6 9 10 11 13 14
		int color = 1 + random.nextInt(11);
		color = color > 6 ? color + 2 : color;
		color = color > 11 ? color + 1 : color;
		return color;
	}

	private void stainFloor(final Block block, final int color)
	{
		savedBlockMat = block.getType();
		savedBlockLoc = block.getLocation();
		savedBlockData = block.getData();
		plugin.getStorage().addUnbreakable(savedBlockLoc, this);
		block.setTypeIdAndData(Material.WOOL.getId(), (byte)color, true);
		if (block.getRelative(BlockFace.UP).getType() == Material.SNOW)
		{
			meltedSnow = true;
			block.getRelative(BlockFace.UP).setType(Material.AIR);
		}
		else
		{
			meltedSnow = false;
		}
		state = State.FLOWING;
		final int span = type.getStainLifeTo() - type.getStainLifeFrom();
		scheduler.scheduleSyncDelayedTask(plugin, this, (span > 0 ? random.nextInt(span) : 0) + type.getStainLifeFrom());
	}

	public void restore()
	{
		if (state == State.SPAWNED)
		{
			state = State.UNKNOWN;
			item.remove();
			plugin.getStorage().removeParticleItem(((CraftItem)item).getUniqueId());
		}
		if (state == State.FLOWING)
		{
			state = State.UNKNOWN;
			restoreBlock(false);
		}
	}

	private void restoreBlock(final boolean removeFromSet)
	{
		plugin.getStorage().removeUnbreakable(savedBlockLoc);
		savedBlockLoc.getBlock().setTypeIdAndData(savedBlockMat.getId(), savedBlockData, false);
		if (meltedSnow)
		{
			savedBlockLoc.getBlock().getRelative(BlockFace.UP).setType(Material.SNOW);
		}
		if (removeFromSet)
		{
			plugin.getStorage().freeParticle(this);
		}
	}
}
