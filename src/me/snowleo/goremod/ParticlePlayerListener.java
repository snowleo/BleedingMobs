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

import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;


class ParticlePlayerListener extends PlayerListener
{
	private final transient IGoreMod goreMod;

	public ParticlePlayerListener(final IGoreMod goreMod)
	{
		super();
		this.goreMod = goreMod;
	}

	@Override
	public void onPlayerPickupItem(final PlayerPickupItemEvent event)
	{
		if (goreMod.isWorldEnabled(event.getPlayer().getWorld())
			&& goreMod.getStorage().isParticleItem(((CraftItem)event.getItem()).getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
}
