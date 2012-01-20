/*
 * BleedingMobs - make your monsters and players bleed
 *
 * Copyright (C) 2011 snowleo
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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkUnloadEvent;


class ParticleProtectionListener implements Listener
{
	private final transient IBleedingMobs plugin;

	public ParticleProtectionListener(final IBleedingMobs plugin)
	{
		super();
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(final BlockBreakEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (plugin.isWorldEnabled(loc.getWorld()) && plugin.getStorage().isUnbreakable(loc))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBurn(final BlockBurnEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (plugin.isWorldEnabled(loc.getWorld()) && plugin.getStorage().isUnbreakable(loc))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockIgnite(final BlockIgniteEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (plugin.isWorldEnabled(loc.getWorld()) && plugin.getStorage().isUnbreakable(loc))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPistonExtend(final BlockPistonExtendEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (!plugin.isWorldEnabled(loc.getWorld()))
		{
			return;
		}
		for (Block block : event.getBlocks())
		{
			if (plugin.getStorage().isUnbreakable(block.getLocation()))
			{
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPistonRetract(final BlockPistonRetractEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (plugin.isWorldEnabled(loc.getWorld()) && plugin.getStorage().isUnbreakable(event.getRetractLocation()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onChunkUnload(final ChunkUnloadEvent event)
	{
		plugin.getStorage().removeParticleItemFromChunk(event.getChunk());
		plugin.getStorage().removeUnbreakableFromChunk(event.getChunk());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		if (event.isCancelled() || !plugin.isWorldEnabled(event.getLocation().getWorld()))
		{
			return;
		}
		for (Block block : event.blockList())
		{
			plugin.getStorage().removeUnbreakableBeforeExplosion(block.getLocation());
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEndermanPickup(final EndermanPickupEvent event)
	{
		if (event.isCancelled() || !plugin.isWorldEnabled(event.getBlock().getWorld()))
		{
			return;
		}
		plugin.getStorage().removeUnbreakableBeforeExplosion(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event)
	{
		if (plugin.isWorldEnabled(event.getPlayer().getWorld())
			&& plugin.getStorage().isParticleItem(((CraftItem)event.getItem()).getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
}
