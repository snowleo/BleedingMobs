package me.snowleo.bleedingmobs;

import java.io.IOException;
import java.util.logging.Level;


public class MetricsStarter implements Runnable
{
	private final transient IBleedingMobs plugin;
	private transient long delay;

	public MetricsStarter(final IBleedingMobs plugin)
	{
		this.plugin = plugin;
		try
		{
			final Metrics metrics = new Metrics();
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
		try
		{
			final Metrics metrics = new Metrics();
			plugin.setMetrics(metrics);
			metrics.addCustomData(plugin, new Metrics.Plotter()
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
			metrics.beginMeasuringPlugin(plugin);

		}
		catch (IOException e)
		{
			plugin.getLogger().log(Level.WARNING, "[Metrics] " + e.getMessage(), e);
		}
	}

	public long getDelay()
	{
		return delay;
	}
}