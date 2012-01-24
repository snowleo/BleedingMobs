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
import org.bukkit.plugin.PluginManager;


public class BleedingMobs extends me.Perdog.BleedingMobs.BleedingMobs implements IBleedingMobs
{
	private transient ParticleStorage storage;
	private transient Settings settings;
	private transient Commands commands;
	private transient Metrics metrics = null;
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

		final PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new ParticleEntityListener(this), this);
		pluginManager.registerEvents(new ParticleProtectionListener(this), this);

		final MetricsStarter metricsStarter = new MetricsStarter(this);
		if (metricsStarter.getDelay() > 0)
		{
			getServer().getScheduler().scheduleAsyncDelayedTask(this, metricsStarter, metricsStarter.getDelay());
		}
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

	@Override
	public Metrics getMetrics()
	{
		return metrics;
	}

	@Override
	public void setMetrics(final Metrics metrics)
	{
		this.metrics = metrics;
	}
}
