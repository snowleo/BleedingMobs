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
package me.snowleo.bleedingmobs.metrics;

import java.io.IOException;
import java.util.logging.Level;
import me.snowleo.bleedingmobs.IBleedingMobs;


public class MetricsStarter implements Runnable
{
	private final transient IBleedingMobs plugin;
	private transient long delay;
	private transient Metrics metrics = null;

	public MetricsStarter(final IBleedingMobs plugin)
	{
		this.plugin = plugin;
		try
		{
			metrics = new Metrics(plugin);
			plugin.setMetrics(metrics);
			if (!metrics.isOptOut())
			{
				if (plugin.getSettings().isShowMetricsInfo())
				{
					plugin.getLogger().info("This plugin collects minimal statistic data and sends it to the server http://metrics.griefcraft.com.");
					plugin.getLogger().info("You can opt out by using the command /bleedingmobs disable-metrics or changing plugins/PluginMetrics/config.yml, set opt-out to true.");
					plugin.getLogger().info("This will start in 5 minutes.");
					delay = 5 * 1200;
				}
				else
				{
					delay = 1;
				}
				return;
			}
		}
		catch (IOException e)
		{
			plugin.getLogger().log(Level.WARNING, "[Metrics] " + e.getMessage(), e);
		}
		delay = -1;
	}

	@Override
	public void run()
	{
		if (metrics == null)
		{
			return;
		}
		metrics.addCustomData(new Metrics.Plotter()
		{
			@Override
			public String getColumnName()
			{
				return "Particles created";
			}

			@Override
			public int getValue()
			{
				final int amount = plugin.getStorage().getParticleStats();
				plugin.getStorage().resetParticleStats();
				return amount;
			}
		});
		metrics.start();
	}

	public long getDelay()
	{
		return delay;
	}
}