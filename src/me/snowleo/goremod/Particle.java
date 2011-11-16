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

import java.util.EnumSet;
import java.util.Random;
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
	private final transient IGoreMod goreMod;
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

	public Particle(final IGoreMod goreMod)
	{
		this.goreMod = goreMod;
		this.scheduler = goreMod.getServer().getScheduler();
	}

	public void start(final Location loc, final ParticleType type)
	{
		this.type = type;
		this.saturatedMats = type.getSaturatedMaterials();
		final int rand = random.nextInt(100);
		int lifetime = random.nextInt(type.getParticleLifeTo() - type.getParticleLifeFrom()) + type.getParticleLifeFrom();
		ItemStack stack;
		if (rand < type.getWoolChance())
		{
			mat = Material.WOOL;
			stack = new ItemStack(mat, 1, (short)type.getWoolColor());
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
		item = loc.getWorld().dropItemNaturally(loc, stack);
		goreMod.getStorage().addParticleItem(((CraftItem)item).getUniqueId(), this);
		state = State.SPAWNED;
		scheduler.scheduleSyncDelayedTask(goreMod, this, lifetime);
	}

	@Override
	public void run()
	{
		if (state == State.SPAWNED)
		{
			if (mat == Material.BONE)
			{
				item.remove();
				goreMod.getStorage().removeParticleItem(((CraftItem)item).getUniqueId());
				goreMod.getStorage().freeParticle(this);
				return;
			}
			if (mat == type.getParticleMaterial() || mat == Material.WOOL)
			{
				Block block = item.getLocation().getBlock();
				if (block == null || block.getType() == Material.AIR || block.getType() == Material.SNOW)
				{
					block = item.getLocation().getBlock().getRelative(BlockFace.DOWN);
				}
				if (block != null && type.isStainingFloor() && saturatedMats.contains(block.getType()) && !goreMod.getStorage().isUnbreakable(block.getLocation()))
				{
					stainFloor(block);
				}
				else
				{
					goreMod.getStorage().freeParticle(this);
				}
				item.remove();
				goreMod.getStorage().removeParticleItem(((CraftItem)item).getUniqueId());
				return;
			}
			Bukkit.getLogger().severe("Invalid particle state! Material: " + mat.toString());
		}
		if (state == State.FLOWING)
		{
			restoreBlock(true);
		}
		else
		{
			goreMod.getStorage().freeParticle(this);
		}
	}

	private void stainFloor(final Block block)
	{
		savedBlockMat = block.getType();
		savedBlockLoc = block.getLocation();
		savedBlockData = block.getData();
		goreMod.getStorage().addUnbreakable(savedBlockLoc, this);
		block.setTypeIdAndData(Material.WOOL.getId(), (byte)type.getWoolColor(), true);
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
		scheduler.scheduleSyncDelayedTask(goreMod, this, random.nextInt(type.getStainLifeTo() - type.getStainLifeFrom()) + type.getStainLifeFrom());
	}

	public void restore()
	{
		if (state == State.SPAWNED)
		{
			state = State.UNKNOWN;
			item.remove();
			goreMod.getStorage().removeParticleItem(((CraftItem)item).getUniqueId());
		}
		if (state == State.FLOWING)
		{
			state = State.UNKNOWN;
			restoreBlock(false);
		}
	}

	private void restoreBlock(final boolean removeFromSet)
	{
		goreMod.getStorage().removeUnbreakable(savedBlockLoc);
		savedBlockLoc.getBlock().setTypeIdAndData(savedBlockMat.getId(), savedBlockData, false);
		if (meltedSnow)
		{
			savedBlockLoc.getBlock().getRelative(BlockFace.UP).setType(Material.SNOW);
		}
		if (removeFromSet)
		{
			goreMod.getStorage().freeParticle(this);
		}
	}
}
