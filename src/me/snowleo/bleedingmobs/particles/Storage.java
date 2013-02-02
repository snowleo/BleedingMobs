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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.tasks.ParticleStateTask;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;


public class Storage
{
	private final Items items;
	private final Unbreakables unbreakables;
	private final IBleedingMobs plugin;
	private final AtomicIntegerArray partStats = new AtomicIntegerArray(6);
	private final AtomicInteger partStatsPos = new AtomicInteger(0);

	public Storage(final IBleedingMobs plugin, final int maxParticles)
	{
		this.plugin = plugin;
		items = new Items(maxParticles);
		unbreakables = new Unbreakables(maxParticles);
	}

	public void createParticles(final LivingEntity entity, final BleedCause cause)
	{
		final Location loc = entity.getLocation();
		if (!plugin.getSettings().isBleedingEnabled() || !plugin.getSettings().isWorldEnabled(loc.getWorld()))
		{
			return;
		}
		final ParticleType type = ParticleType.get(entity.getType());
		if (type == null)
		{
			return;
		}

		final UUID worldId = loc.getWorld().getUID();
		final int percentage = cause.getPercentages(plugin.getSettings());
		final int amount = Util.getRandomBetween(type.getAmountFrom(), type.getAmountTo()) * percentage / 100;
		for (int i = 0; i < amount; i++)
		{
			if (items.testLimit(worldId))
			{
				partStats.incrementAndGet(partStatsPos.get());
				ParticleStateTask particleStateTask = new ParticleStateTask(plugin, type, loc, cause);
				particleStateTask.start();
			}
		}
		if (cause != BleedCause.BLOODSTREAM)
		{
			plugin.getTimer().add(entity);
		}
	}

	public void clearAllParticles()
	{
		items.restoreAll();
		unbreakables.restoreAll();
	}

	public boolean isParticleItem(final UUID id)
	{
		return items.contains(id);
	}

	public Items getItems()
	{
		return items;
	}

	public Unbreakables getUnbreakables()
	{
		return unbreakables;
	}

	public void resetParticleStats()
	{
		if (partStatsPos.incrementAndGet() >= partStats.length())
		{
			partStatsPos.set(0);
		}
		partStats.set(partStatsPos.get(), 0);
	}

	public int getParticleStats()
	{
		int amount = 0;
		for (int i = 0; i < partStats.length(); i++)
		{
			amount += partStats.get(i);
		}
		return amount;
	}
}
