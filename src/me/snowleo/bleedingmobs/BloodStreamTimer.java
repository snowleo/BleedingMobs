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
package me.snowleo.bleedingmobs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.entity.LivingEntity;


public class BloodStreamTimer implements Runnable
{
	private final transient Map<LivingEntity, Integer> entities = new HashMap<LivingEntity, Integer>();
	private final transient IBleedingMobs plugin;

	public BloodStreamTimer(final IBleedingMobs plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void run()
	{
		synchronized (entities)
		{
			final int interval = plugin.getSettings().getBloodstreamInterval();
			final Iterator<Map.Entry<LivingEntity, Integer>> iterator = entities.entrySet().iterator();
			while (iterator.hasNext())
			{
				final Map.Entry<LivingEntity, Integer> entry = iterator.next();
				int timeleft = entry.getValue();
				timeleft -= interval;
				final LivingEntity entity = entry.getKey();
				if (timeleft > 0 && !entity.isDead() && entity.getLocation() != null)
				{
					plugin.getStorage().createParticle(entity, ParticleStorage.BleedCause.BLOODSTREAM);
					entry.setValue(timeleft);
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
			entities.put(entity, plugin.getSettings().getBloodstreamTime());
		}
	}

	public void remove(final LivingEntity entity)
	{
		synchronized (entities)
		{
			entities.remove(entity);
		}
	}
}
