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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.snowleo.bleedingmobs.tasks.ParticleStateTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;


public class Items
{
	private final TaskMap<UUID> items;
	private final Map<UUID, Integer> particlesPerWorld = Collections.synchronizedMap(new HashMap<UUID, Integer>());
	private volatile int limitPerWorld = Util.COUNTER_SIZE;

	public Items(final int maxParticles)
	{
		items = new TaskMap<UUID>(maxParticles);
		setLimit(maxParticles);
	}

	public final void setLimit(final int limitPerWorld)
	{
		this.limitPerWorld = Math.max(1, Math.min(Util.COUNTER_SIZE, limitPerWorld));
	}

	public void add(final Item item, final ParticleStateTask particleTask)
	{
		items.add(item.getUniqueId(), particleTask);
		addToLimit(item);
	}

	public void restoreAll()
	{
		items.restoreAll();
		particlesPerWorld.clear();
	}

	public boolean testLimit(final UUID worldId)
	{
		Integer c = particlesPerWorld.get(worldId);
		return c == null || c < limitPerWorld;
	}

	public void remove(final Item item)
	{
		boolean found = items.remove(item.getUniqueId());
		if (found)
		{
			removeFromLimit(item);
		}
	}

	public void restore(final Item item)
	{
		boolean found = items.restore(item.getUniqueId());
		if (found)
		{
			removeFromLimit(item);
		}
	}

	public int getCurrentParticleAmount()
	{
		int amount = 0;
		synchronized (particlesPerWorld)
		{
			for (Integer integer : particlesPerWorld.values())
			{
				amount += integer.intValue();
			}
		}
		return amount;
	}

	private void removeFromLimit(final Item item)
	{
		final UUID worldId = item.getWorld().getUID();
		synchronized (particlesPerWorld)
		{
			Integer c = particlesPerWorld.get(worldId);
			if (c == null || c <= 0)
			{
				Bukkit.getLogger().info("Item at " + item.getLocation() + " with value " + item.getItemStack());
				throw new IllegalStateException();
			}
			else
			{
				particlesPerWorld.put(worldId, Integer.valueOf(c - 1));
			}
		}
	}

	private void addToLimit(final Item item)
	{
		final UUID worldId = item.getWorld().getUID();
		synchronized (particlesPerWorld)
		{
			Integer c = particlesPerWorld.get(worldId);
			int i = c == null ? 1 : c + 1;
			particlesPerWorld.put(worldId, Integer.valueOf(i));
		}
	}

	public boolean contains(final UUID uuid)
	{
		return items.contains(uuid);
	}

	public int getLimit()
	{
		return limitPerWorld;
	}
}
