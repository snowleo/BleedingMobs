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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class BleedingMobs extends JavaPlugin implements IBleedingMobs
{
	private transient ParticleStorage storage;
	private transient Settings settings;
	private transient Commands commands;
	private transient Metrics metrics = null;
	private transient boolean spawning = false;
	private transient BloodStreamTimer bloodStreamTimer;
	private transient int timerId = -1;
	private transient boolean cbPlusPlus = false;

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
		final Plugin bkcommonlib = pluginManager.getPlugin("BKCommonLib");
		if (bkcommonlib != null)
		{
			final String version = bkcommonlib.getDescription().getVersion();
			if (version.equals("1.0") || version.equals("1.01") || version.equals("1.02") || version.equals("1.03") || version.equals("1.04") || version.equals("1.05"))
			{
				getLogger().log(Level.SEVERE, "Conflicting version of BKCommonLib (NoLagg) found, please update it. BleedingMobs is now disabled.");
				setEnabled(false);
				return;
			}
		}

		cbPlusPlus = getServer().getVersion().contains("CraftBukkit++");

		if (cbPlusPlus)
		{
			try
			{
				final Method itemMergeRadiusMethod = getServer().getClass().getMethod("setItemMergeRadius", double.class);
				itemMergeRadiusMethod.invoke(getServer(), 0.0);
				getLogger().log(Level.INFO, "CraftBukkit++ detected. Item Merge Radius set to 0.0.");
			}
			catch (NoSuchMethodException ex)
			{
				// Ignore this, method has been removed in newer versions of CraftBukkit++
			}
			catch (Exception ex)
			{
				getLogger().log(Level.SEVERE, "Failed to inject into CraftBukkit++. BleedingMobs is now disabled.", ex);
				setEnabled(false);
				return;
			}
			if (!removeItemMergeRadiusCommand())
			{
				return;
			}
		}

		settings = new Settings(this);
		if (cbPlusPlus)
		{
			for (World world : getServer().getWorlds())
			{
				if (!setItemMergeRadius(world))
				{
					return;
				}
			}
		}
		storage = new ParticleStorage(this, settings.getMaxParticles());
		commands = new Commands(this);
		bloodStreamTimer = new BloodStreamTimer(this);

		pluginManager.registerEvents(new ParticleEntityListener(this), this);
		pluginManager.registerEvents(new ParticleProtectionListener(this), this);

		final MetricsStarter metricsStarter = new MetricsStarter(this);
		if (metricsStarter.getDelay() > 0)
		{
			getServer().getScheduler().scheduleAsyncDelayedTask(this, metricsStarter, metricsStarter.getDelay());
		}

		restartTimer();
	}

	private boolean removeItemMergeRadiusCommand()
	{
		try
		{
			final Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			final SimpleCommandMap scm = (SimpleCommandMap)commandMapField.get(getServer());
			final Field mapField = scm.getClass().getDeclaredField("knownCommands");
			mapField.setAccessible(true);
			final Map<String, Command> map = (Map<String, Command>)mapField.get(scm);
			for (final Iterator<Map.Entry<String, Command>> it = map.entrySet().iterator(); it.hasNext();)
			{
				final Map.Entry<String, Command> entry = it.next();
				if (entry.getValue().getClass().getName().contains("ItemMergeRadiusCommand"))
				{
					it.remove();
				}
			}
			getLogger().log(Level.INFO, "CraftBukkit++ detected. Disabled Item Merge Radius Command.");
		}
		catch (Exception ex)
		{
			getLogger().log(Level.SEVERE, "Failed to inject into CraftBukkit++. BleedingMobs is now disabled.", ex);
			setEnabled(false);
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemMergeRadius(final World world)
	{
		if (cbPlusPlus)
		{
			try
			{
				if (!settings.isWorldEnabled(world))
				{
					return true;
				}
				final Method itemMergeRadiusMethod = world.getClass().getMethod("setItemMergeRadius", double.class);
				itemMergeRadiusMethod.invoke(world, 0.0);
				getLogger().log(Level.INFO, "CraftBukkit++ detected. Item Merge Radius set to 0.0 in world " + world.getName() + ".");
			}
			catch (NoSuchMethodException ex)
			{
				// Ignore this, method is only available in newer versions of CraftBukkit++
			}
			catch (Exception ex)
			{
				getLogger().log(Level.SEVERE, "Failed to inject into CraftBukkit++. BleedingMobs is now disabled.", ex);
				setEnabled(false);
				return false;
			}
		}
		return true;
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

	@Override
	public void restartTimer()
	{
		if (timerId >= 0)
		{
			getServer().getScheduler().cancelTask(timerId);
		}
		if (settings.getBloodstreamTime() > 0 && settings.getBloodstreamPercentage() > 0)
		{
			timerId = getServer().getScheduler().scheduleSyncRepeatingTask(this, bloodStreamTimer, settings.getBloodstreamInterval(), settings.getBloodstreamInterval());
		}
	}

	@Override
	public BloodStreamTimer getTimer()
	{
		return bloodStreamTimer;
	}

	/**
	 *
	 * @deprecated Use getStorage().isParticleItem(id) instead.
	 *
	 */
	@Override
	@Deprecated
	public boolean isParticleItem(final UUID uuid)
	{
		return getStorage().isParticleItem(uuid);
	}
}
