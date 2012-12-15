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
package me.snowleo.bleedingmobs.listener;

import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.tasks.BloodStreamTask;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkUnloadEvent;


public class ParticleProtectionListener implements Listener
{
	private final transient IBleedingMobs plugin;

	public ParticleProtectionListener(final IBleedingMobs plugin)
	{
		super();
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (plugin.getSettings().isWorldEnabled(loc.getWorld())
			&& plugin.getStorage().getUnbreakables().contains(loc))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockBurn(final BlockBurnEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (plugin.getSettings().isWorldEnabled(loc.getWorld())
			&& plugin.getStorage().getUnbreakables().contains(loc))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockIgnite(final BlockIgniteEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (plugin.getSettings().isWorldEnabled(loc.getWorld())
			&& plugin.getStorage().getUnbreakables().contains(loc))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPistonExtend(final BlockPistonExtendEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (!plugin.getSettings().isWorldEnabled(loc.getWorld()))
		{
			return;
		}
		for (Block block : event.getBlocks())
		{
			if (plugin.getStorage().getUnbreakables().contains(block.getLocation()))
			{
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPistonRetract(final BlockPistonRetractEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (plugin.getSettings().isWorldEnabled(loc.getWorld())
			&& plugin.getStorage().getUnbreakables().contains(event.getRetractLocation()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onChunkUnload(final ChunkUnloadEvent event)
	{
		final Entity[] entities = event.getChunk().getEntities();
		final BloodStreamTask timer = plugin.getTimer();
		for (Entity entity : entities)
		{
			if (entity instanceof Item)
			{
				plugin.getStorage().getItems().restore((Item)entity);
			}
			if (entity instanceof LivingEntity)
			{
				timer.remove((LivingEntity)entity);
			}
		}
		plugin.getStorage().getUnbreakables().removeByChunk(event.getChunk());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		if (!plugin.getSettings().isWorldEnabled(event.getLocation().getWorld()))
		{
			return;
		}
		for (Block block : event.blockList())
		{
			plugin.getStorage().getUnbreakables().restore(block.getLocation());
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityChangeBlock(final EntityChangeBlockEvent event)
	{
		if (!plugin.getSettings().isWorldEnabled(event.getBlock().getWorld()))
		{
			return;
		}
		plugin.getStorage().getUnbreakables().restore(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event)
	{
		if (plugin.getSettings().isWorldEnabled(event.getPlayer().getWorld())
			&& plugin.getStorage().getItems().contains(event.getItem().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityTeleport(final EntityTeleportEvent event)
	{
		if (event.getEntityType() == EntityType.DROPPED_ITEM
			&& plugin.getSettings().isWorldEnabled(event.getFrom().getWorld())
			&& plugin.getStorage().getItems().contains(event.getEntity().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityCombust(final EntityCombustEvent event)
	{
		if (event.getEntityType() == EntityType.DROPPED_ITEM
			&& plugin.getSettings().isWorldEnabled(event.getEntity().getWorld())
			&& plugin.getStorage().getItems().contains(event.getEntity().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
}
