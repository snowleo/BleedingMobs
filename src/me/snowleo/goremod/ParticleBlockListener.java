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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;


class ParticleBlockListener extends BlockListener
{
	private final transient IGoreMod goreMod;

	public ParticleBlockListener(final IGoreMod goreMod)
	{
		this.goreMod = goreMod;
	}

	@Override
	public void onBlockBreak(final BlockBreakEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (goreMod.isWorldEnabled(loc.getWorld()) && goreMod.isUnbreakable(loc))
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockBurn(final BlockBurnEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (goreMod.isWorldEnabled(loc.getWorld()) && goreMod.isUnbreakable(loc))
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockIgnite(final BlockIgniteEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (goreMod.isWorldEnabled(loc.getWorld()) && goreMod.isUnbreakable(loc))
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockPistonExtend(final BlockPistonExtendEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (!goreMod.isWorldEnabled(loc.getWorld()))
		{
			return;
		}
		for (Block block : event.getBlocks())
		{
			if (goreMod.isUnbreakable(loc))
			{
				event.setCancelled(true);
				break;
			}
		}
	}

	@Override
	public void onBlockPistonRetract(final BlockPistonRetractEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (goreMod.isWorldEnabled(loc.getWorld()) && goreMod.isUnbreakable(event.getRetractLocation()))
		{
			event.setCancelled(true);
		}
	}
}
