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

import java.util.UUID;
import me.snowleo.bleedingmobs.commands.RootCommand;
import me.snowleo.bleedingmobs.listener.ParticleEntityListener;
import me.snowleo.bleedingmobs.listener.ParticleProtectionListener;
import me.snowleo.bleedingmobs.particles.Storage;
import me.snowleo.bleedingmobs.tasks.BloodStreamTask;
import me.snowleo.bleedingmobs.update.UpdateNotifier;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class BleedingMobs extends JavaPlugin implements IBleedingMobs
{
	private volatile Storage storage;
	private volatile Settings settings;
	private volatile boolean spawning = false;
	private volatile BloodStreamTask bloodStreamTimer;
	private volatile BukkitTask timer = null;

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
		final PluginManager pluginManager = getServer().getPluginManager();
		settings = new Settings(this);
		UpdateNotifier notifier = new UpdateNotifier(this);
		if (settings.isCheckForUpdates() == true)
		{
			notifier.check();
		}
		storage = new Storage(this, settings.getMaxParticles());
		PluginCommand command = (PluginCommand)this.getCommand("bleedingmobs");
		RootCommand rootCommand = new RootCommand(this);
		command.setExecutor(rootCommand);
		command.setTabCompleter(rootCommand);
		bloodStreamTimer = new BloodStreamTask(this);

		pluginManager.registerEvents(new ParticleEntityListener(this), this);
		pluginManager.registerEvents(new ParticleProtectionListener(this), this);
		pluginManager.registerEvents(notifier, this);

		restartTimer();
	}

	@Override
	public Storage getStorage()
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

	@Override
	public Settings getSettings()
	{
		return settings;
	}

	@Override
	public void restartTimer()
	{
		if (timer != null)
		{
			timer.cancel();
		}
		if (settings.getBloodstreamTime() > 0 && settings.getBloodstreamPercentage() > 0)
		{
			timer = getServer().getScheduler().runTaskTimer(this, bloodStreamTimer, settings.getBloodstreamInterval(), settings.getBloodstreamInterval());
		}
	}

	@Override
	public BloodStreamTask getTimer()
	{
		return bloodStreamTimer;
	}

	@Override
	public boolean isParticleItem(final UUID uuid)
	{
		return getStorage().isParticleItem(uuid);
	}
}
