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
package me.snowleo.bleedingmobs.tasks;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.particles.BleedCause;
import org.bukkit.entity.LivingEntity;


public class BloodStreamTask implements Runnable
{
	private final transient Map<UUID, BleedingEntity> entities = new HashMap<UUID, BleedingEntity>();
	private final transient IBleedingMobs plugin;


	private class BleedingEntity
	{
		public WeakReference<LivingEntity> entity;
		public int timeleft;

		public BleedingEntity(LivingEntity entity, int timeleft)
		{
			this.entity = new WeakReference<LivingEntity>(entity);
			this.timeleft = timeleft;
		}
	}

	public BloodStreamTask(final IBleedingMobs plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void run()
	{
		synchronized (entities)
		{
			final int interval = plugin.getSettings().getBloodstreamInterval();
			final Iterator<BleedingEntity> iterator = entities.values().iterator();
			while (iterator.hasNext())
			{
				final BleedingEntity entry = iterator.next();
				entry.timeleft -= interval;
				final LivingEntity entity = entry.entity.get();
				if (entry.timeleft > 0 && entity != null && !entity.isDead() && entity.getLocation() != null)
				{
					plugin.getStorage().createParticles(entity, BleedCause.BLOODSTREAM);
				}
				else
				{
					iterator.remove();
				}
			}
		}
	}

	public void add(final LivingEntity entity)
	{
		synchronized (entities)
		{
			entities.put(entity.getUniqueId(), new BleedingEntity(entity, plugin.getSettings().getBloodstreamTime()));
		}
	}

	public void remove(final LivingEntity entity)
	{
		synchronized (entities)
		{
			entities.remove(entity.getUniqueId());
		}
	}
}
