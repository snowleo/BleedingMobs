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

import java.util.UUID;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.world.WorldListener;
import org.bukkit.plugin.PluginManager;


public class BleedingMobs extends me.Perdog.BleedingMobs.BleedingMobs implements IBleedingMobs
{
	private transient ParticleStorage storage;
	private transient Settings settings;
	private transient Commands commands;
	private transient boolean spawning = false;

	@Override
	public void onDisable()
	{
		if (storage != null)
		{
			storage.clearAllParticles();
		}
	}

	@Override
	public void onEnable()
	{
		settings = new Settings(this);
		storage = new ParticleStorage(this, settings.getMaxParticles());
		commands = new Commands(this);

		registerListeners();

		final String loadMessage = getDescription().getFullName() + " loaded. Have fun!";
		getServer().getLogger().info(loadMessage);
	}

	private void registerListeners()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		final EntityListener entityListener = new ParticleEntityListener(this);
		pluginManager.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Monitor, this);
		pluginManager.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Low, this);
		pluginManager.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
		pluginManager.registerEvent(Type.ENDERMAN_PICKUP, entityListener, Priority.Low, this);
		final PlayerListener playerListener = new ParticlePlayerListener(this);
		pluginManager.registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Low, this);
		final BlockListener blockListener = new ParticleBlockListener(this);
		pluginManager.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Low, this);
		pluginManager.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Low, this);
		pluginManager.registerEvent(Type.BLOCK_IGNITE, blockListener, Priority.Low, this);
		final WorldListener worldListener = new ParticleWorldListener(this);
		pluginManager.registerEvent(Type.CHUNK_UNLOAD, worldListener, Priority.Low, this);

		pluginManager.registerEvent(Type.BLOCK_PISTON_EXTEND, blockListener, Priority.Low, this);
		pluginManager.registerEvent(Type.BLOCK_PISTON_RETRACT, blockListener, Priority.Low, this);
	}

	@Override
	public ParticleStorage getStorage()
	{
		return storage;
	}

	@Override
	public boolean isWorldEnabled(final World world)
	{
		return settings.isWorldEnabled(world);
	}

	@Override
	public boolean isSpawning()
	{
		return spawning;
	}

	@Override
	public void setSpawning(final boolean spawning)
	{
		this.spawning = spawning;
	}

	/**
	 * @deprecated Use getStorage().isParticleItem(id) instead.
	 */
	@Override
	@Deprecated
	public boolean isParticleItem(final UUID uuid)
	{
		return getStorage().isParticleItem(uuid);
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
							 final String label, final String[] args)
	{
		if (args.length < 1)
		{
			sender.sendMessage(command.getDescription());
			sender.sendMessage(command.getUsage());
		}
		else if (sender.hasPermission("bleedingmobs.admin"))
		{
			commands.run(sender, args);
		}
		else
		{
			sender.sendMessage("You need the permission bleedingmobs.admin to run this command.");
		}
		return true;
	}

	@Override
	public Settings getSettings()
	{
		return settings;
	}
}
