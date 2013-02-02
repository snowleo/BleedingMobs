/*
 * BleedingMobs - make your monsters and players bleed
 *
 * Copyright (C) 2011-2012 snowleo
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
package me.snowleo.bleedingmobs.particles;

import me.snowleo.bleedingmobs.IBleedingMobs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;


public class Particle
{
	private final IBleedingMobs plugin;
	private final ParticleType type;
	private final ItemStack stack;
	private final int lifetime;
	private final Item item;

	public Particle(final IBleedingMobs plugin, final Location loc, final ParticleType type, final boolean bones)
	{
		this.plugin = plugin;
		this.type = type;
		DropType dropType = DropType.getRandom(type, bones);
		lifetime = estimateLifetime(dropType);
		stack = createStack(dropType);
		makeUnique(loc.getWorld());
		item = dropItem(loc.clone());
	}

	private int estimateLifetime(final DropType dropType)
	{
		int ticks = Util.getRandomBetween(type.getParticleLifeFrom(), type.getParticleLifeTo());
		if (dropType == DropType.BONE)
		{
			ticks += type.getBoneLife();
		}
		return ticks;
	}

	private ItemStack createStack(final DropType dropType)
	{
		switch (dropType)
		{
		case WOOL:
			return createWoolStack();
		case BONE:
			return new ItemStack(Material.BONE, 1);
		default:
		case PARTICLE:
			return createParticleMaterialStack();
		}
	}

	private ItemStack createWoolStack()
	{
		if (type.isMagicMaterial())
		{
			return new ItemStack(Material.WOOL, 1, Util.getRandomColor());
		}
		else
		{
			return new ItemStack(Material.WOOL, 1, type.getWoolColor().getData());
		}
	}

	private ItemStack createParticleMaterialStack()
	{
		Material mat = type.getParticleMaterial().getItemType();
		byte data = type.getParticleMaterial().getData();
		if (!Util.isAllowedMaterial(mat))
		{
			mat = Material.REDSTONE;
			data = 0;
		}
		return new ItemStack(mat, 1, data);
	}

	public boolean isStainingMaterial()
	{
		final Material mat = stack.getType();
		return mat == type.getParticleMaterial().getItemType() || mat == Material.WOOL;
	}

	private void makeUnique(final World world)
	{
		stack.addUnsafeEnchantment(Enchantment.DURABILITY, Util.getCounter(world.getUID()));
	}

	private Item dropItem(final Location loc)
	{
		plugin.setSpawning(true);
		try
		{
			// Fix CraftBukkit drop location
			loc.setX(loc.getX() - 0.5);
			loc.setZ(loc.getZ() - 0.5);
			return loc.getWorld().dropItemNaturally(loc, stack);
		}
		finally
		{
			plugin.setSpawning(false);
		}
	}

	public int getLifetime()
	{
		return lifetime;
	}

	public Item getItem()
	{
		return item;
	}

	public void restore()
	{
		item.remove();
	}
}
