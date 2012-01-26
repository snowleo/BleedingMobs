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

import java.util.Random;
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
	private transient Item item;
	private final transient BukkitScheduler scheduler;
	private final transient IBleedingMobs plugin;
	private final transient Random random = new Random();
	private transient ParticleType type = ParticleType.ATTACK;
	private transient boolean meltedSnow;
	private transient byte snowData;
	private transient Location startLocation;
	private transient ItemStack stack;
	private transient int taskId;


	enum State
	{
		UNKNOWN,
		INIT,
		SPAWNED,
		FLOWING
	}
	private transient State state = State.UNKNOWN;
	private transient Material savedBlockMat;
	private transient Location savedBlockLoc;
	private transient byte savedBlockData;

	public Particle(final IBleedingMobs plugin)
	{
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
	}

	public void start(final Location loc, final ParticleType type)
	{
		this.type = type;
		this.startLocation = loc;
		final int rand = random.nextInt(100);
		final int span = type.getParticleLifeTo() - type.getParticleLifeFrom();
		int lifetime = (span > 0 ? random.nextInt(span) : 0) + type.getParticleLifeFrom();
		if (rand < type.getWoolChance())
		{
			if (type.getParticleMaterial() == Material.CAKE)
			{
				stack = new ItemStack(Material.WOOL, 1, getRandomColor());
			}
			else
			{
				stack = new ItemStack(Material.WOOL, 1, type.getWoolColor().getData());
			}
		}
		else if (rand > (99 - type.getBoneChance()))
		{
			stack = new ItemStack(Material.BONE, 1);
			lifetime += type.getBoneLife();
		}
		else
		{
			stack = new ItemStack(type.getParticleMaterial(), 1);
		}
		state = State.INIT;
		taskId = scheduler.scheduleSyncRepeatingTask(plugin, this, 0, lifetime);
	}

	private void init()
	{
		plugin.setSpawning(true);
		item = startLocation.getWorld().dropItemNaturally(startLocation, stack);
		plugin.setSpawning(false);
		plugin.getStorage().addParticleItem(((CraftItem)item).getUniqueId(), this);
		state = State.SPAWNED;
		startLocation = null;
	}

	@Override
	public void run()
	{
		if (state == State.INIT)
		{
			init();
		}
		else if (state == State.SPAWNED)
		{
			if (taskId > 0)
			{
				plugin.getServer().getScheduler().cancelTask(taskId);
				taskId = -1;
			}
			final Material mat = stack.getType();
			if (mat == type.getParticleMaterial() || mat == Material.WOOL)
			{
				Block block = item.getLocation().getBlock();

				if (block == null
					|| block.getType() == Material.AIR
					|| block.getType() == Material.SNOW
					|| block.getType() == Material.WATER
					|| block.getType() == Material.STATIONARY_WATER)
				{
					block = item.getLocation().subtract(0, 1, 0).getBlock();
				}
				if (block != null && type.isStainingFloor() && type.getSaturatedMaterials().contains(block.getType()) && !plugin.getStorage().isUnbreakable(block.getLocation()))
				{
					if (type.getParticleMaterial() == Material.CAKE)
					{
						stainFloor(block, getRandomColor());
					}
					else
					{
						stainFloor(block, type.getWoolColor().getData());
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
			item.remove();
			plugin.getStorage().removeParticleItem(((CraftItem)item).getUniqueId());
			plugin.getStorage().freeParticle(this);
		}
		else if (state == State.FLOWING)
		{
			restoreBlock(true, true);
		}
		else
		{
			plugin.getStorage().freeParticle(this);
		}
	}

	private byte getRandomColor()
	{
		// 1 2 3 4 5 6 9 10 11 13 14
		int color = 1 + random.nextInt(11);
		color = color > 6 ? color + 2 : color;
		color = color > 11 ? color + 1 : color;
		return (byte)color;
	}

	private void stainFloor(final Block block, final byte color)
	{
		savedBlockMat = block.getType();
		savedBlockLoc = block.getLocation();
		savedBlockData = block.getData();
		plugin.getStorage().addUnbreakable(savedBlockLoc, this);
		block.setTypeIdAndData(Material.WOOL.getId(), color, false);
		final Block snowBlock = block.getRelative(BlockFace.UP);
		if (snowBlock.getType() == Material.SNOW)
		{
			meltedSnow = true;
			snowData = snowBlock.getData();
			snowBlock.setTypeIdAndData(0, (byte)0, false);
		}
		else
		{
			meltedSnow = false;
		}
		state = State.FLOWING;
		final int span = type.getStainLifeTo() - type.getStainLifeFrom();
		scheduler.scheduleSyncDelayedTask(plugin, this, (span > 0 ? random.nextInt(span) : 0) + type.getStainLifeFrom());
	}

	public void restore(final boolean removeFromUnbreakable)
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
			restoreBlock(false, removeFromUnbreakable);
		}
	}

	private void restoreBlock(final boolean removeFromSet, final boolean removeFromUnbreakable)
	{
		if (removeFromUnbreakable)
		{
			plugin.getStorage().removeUnbreakable(savedBlockLoc);
		}
		savedBlockLoc.getBlock().setTypeIdAndData(savedBlockMat.getId(), savedBlockData, false);
		if (meltedSnow)
		{
			savedBlockLoc.getBlock().getRelative(BlockFace.UP).setTypeIdAndData(Material.SNOW.getId(), snowData, false);
		}
		if (removeFromSet)
		{
			plugin.getStorage().freeParticle(this);
		}
	}
}
