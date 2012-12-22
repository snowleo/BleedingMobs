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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Material;


public final class Util
{
	private final static Random RANDOM = new Random();
	private final static Map<UUID, Integer> counter = new HashMap<UUID, Integer>();
	public final static int COUNTER_MIN = 2767;
	public final static int COUNTER_MAX = 32767;
	public final static int COUNTER_SIZE = COUNTER_MAX - COUNTER_MIN;

	private Util()
	{
	}

	public static int getRandomBetween(int from, int to)
	{
		final int span = to - from;
		return (span > 0 ? RANDOM.nextInt(span) : 0) + from;
	}

	public static byte getRandomColor()
	{
		// 1 2 3 4 5 6 9 10 11 13 14
		int color = 1 + RANDOM.nextInt(11);
		color = color > 6 ? color + 2 : color;
		color = color > 11 ? color + 1 : color;
		return (byte)color;
	}

	public static boolean isAllowedMaterial(Material mat)
	{
		return mat.getMaxDurability() == 0 && mat != Material.PUMPKIN && mat != Material.SKULL_ITEM && mat != Material.SKULL;
	}

	public static int getCounter(final UUID worldId)
	{
		synchronized (counter)
		{
			Integer c = counter.get(worldId);
			int r = c == null ? COUNTER_MIN : (c >= COUNTER_MAX ? COUNTER_MIN : c + 1);
			counter.put(worldId, Integer.valueOf(r));
			return r;
		}
	}
}
