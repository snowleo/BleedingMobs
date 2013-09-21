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
import me.snowleo.bleedingmobs.tasks.ParticleStateTask;


public class TaskMap<K>
{
	private final Map<K, ParticleStateTask> particleMap;

	public TaskMap(final int maxParticles)
	{
		particleMap = Collections.synchronizedMap(new HashMap<K, ParticleStateTask>(maxParticles));
	}

	public void add(final K key, final ParticleStateTask particleTask)
	{
		if (key != null)
		{
			particleMap.put(key, particleTask);
		}
	}

	public boolean remove(final K key)
	{
		if (key != null)
		{
			return particleMap.remove(key) != null;
		}
		return false;
	}

	public boolean restore(final K key)
	{
		final ParticleStateTask particleStateTask = particleMap.get(key);
		if (particleStateTask != null)
		{
			particleStateTask.restore();
			return particleMap.remove(key) != null;
		}
		return false;
	}

	public boolean contains(final K key)
	{
		return particleMap.containsKey(key);
	}

	public void restoreAll()
	{
		synchronized (particleMap)
		{
			for (ParticleStateTask particleStateTask : particleMap.values())
			{
				particleStateTask.restore();
			}
		}
		particleMap.clear();
	}

	protected Map<K, ParticleStateTask> getParticleMap()
	{
		return particleMap;
	}
}
