package me.snowleo.bleedingmobs;

/*
 * Copyright 2011 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and contributors and should not be interpreted as
 * representing official policies, either expressed or implied, of anybody else.
 */
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


/**
 * Tooling to post to metrics.griefcraft.com
 */
public class Metrics
{
	/**
	 * Interface used to collect custom data for a plugin
	 */
	public static abstract class Plotter
	{
		/**
		 * Get the column name for the plotted point
		 *
		 * @return the plotted point's column name
		 */
		public abstract String getColumnName();

		/**
		 * Get the current value for the plotted point
		 *
		 * @return
		 */
		public abstract int getValue();

		@Override
		public int hashCode()
		{
			return getColumnName().hashCode() + getValue();
		}

		@Override
		public boolean equals(final Object object)
		{
			if (object instanceof Plotter)
			{
				final Plotter plotter = (Plotter)object;
				return plotter.getColumnName().equals(getColumnName()) && plotter.getValue() == getValue();
			}
			else
			{
				return false;
			}
		}
	}
	/**
	 * The metrics revision number
	 */
	private static final int REVISION = 3;
	/**
	 * The base url of the metrics domain
	 */
	private static final String BASE_URL = "http://metrics.griefcraft.com";
	/**
	 * The url used to report a server's status
	 */
	private static final String REPORT_URL = "/report/%s";
	/**
	 * The file where guid and opt out is stored in
	 */
	private static final String CONFIG_FILE = "plugins/PluginMetrics/config.yml";
	/**
	 * Interval of time to ping in minutes
	 */
	private static final int PING_INTERVAL = 10;
	/**
	 * A map of the custom data plotters for plugins
	 */
	private final transient Map<Plugin, Set<Plotter>> customData = Collections.synchronizedMap(new HashMap<Plugin, Set<Plotter>>());
	/**
	 * The plugin configuration file
	 */
	private final transient YamlConfiguration configuration;
	/**
	 * Unique server id
	 */
	private final transient String guid;
	
	private static final String GUID = "guid";
	
	private static final String OPTOUT = "opt-out";
	
	private final transient boolean optOut;
	
	private transient int taskId = -1;

	public Metrics() throws IOException
	{
		// load the config
		final File file = new File(CONFIG_FILE);
		configuration = YamlConfiguration.loadConfiguration(file);

		// add some defaults
		configuration.addDefault(OPTOUT, false);
		configuration.addDefault(GUID, UUID.randomUUID().toString());

		// Do we need to create the file?
		if (configuration.get(GUID, null) == null)
		{
			configuration.options().header("http://metrics.griefcraft.com").copyDefaults(true);
			configuration.save(file);
		}

		// Load the guid then
		guid = configuration.getString(GUID);
		optOut = configuration.getBoolean(OPTOUT, false);
	}

	public boolean isOptOut()
	{
		return optOut;
	}

	/**
	 * Adds a custom data plotter for a given plugin
	 *
	 * @param plugin
	 * @param plotter
	 */
	public void addCustomData(final Plugin plugin, final Plotter plotter)
	{
		Set<Plotter> plotters = customData.get(plugin);

		if (plotters == null)
		{
			plotters = Collections.synchronizedSet(new LinkedHashSet<Plotter>());
			customData.put(plugin, plotters);
		}

		plotters.add(plotter);
	}

	/**
	 * Begin measuring a plugin
	 *
	 * @param plugin
	 */
	public void beginMeasuringPlugin(final Plugin plugin) throws IOException
	{
		// Did we opt out?
		if (isOptOut())
		{
			return;
		}

		// First tell the server about us
		postPlugin(plugin, false);

		// Ping the server in intervals
		taskId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					postPlugin(plugin, true);
				}
				catch (IOException e)
				{
					plugin.getLogger().log(Level.WARNING, "[Metrics] " + e.getMessage(), e);
				}
			}
		}, PING_INTERVAL * 1200, PING_INTERVAL * 1200);
	}

	/**
	 * Generic method that posts a plugin to the metrics website
	 *
	 * @param plugin
	 */
	private void postPlugin(final Plugin plugin, final boolean isPing) throws IOException
	{
		// Construct the post data
		String response;
		final StringBuilder data = new StringBuilder();
		data.append(encode(GUID)).append('=').append(encode(guid));
		data.append('&').append(encode("version")).append('=').append(encode(plugin.getDescription().getVersion()));
		data.append('&').append(encode("server")).append('=').append(encode(Bukkit.getVersion()));
		data.append('&').append(encode("players")).append('=').append(encode(Integer.toString(Bukkit.getServer().getOnlinePlayers().length)));
		data.append('&').append(encode("revision")).append('=').append(encode(Integer.toString(REVISION)));

		// If we're pinging, append it
		if (isPing)
		{
			data.append('&').append(encode("ping")).append('=').append(encode("true"));
		}

		// Add any custom data (if applicable)
		final Set<Plotter> plotters = customData.get(plugin);

		if (plotters != null)
		{
			for (Plotter plotter : plotters)
			{
				data.append('&').append(encode("Custom" + plotter.getColumnName()));
				data.append('=').append(encode(Integer.toString(plotter.getValue())));
			}
		}

		// Create the url
		final URL url = new URL(BASE_URL + String.format(REPORT_URL, plugin.getDescription().getName()));

		// Connect to the website
		final URLConnection connection = url.openConnection();
		connection.setDoOutput(true);

		// Write the data
		final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(data.toString());
		writer.flush();

		// Now read the response
		final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		response = reader.readLine();

		// close resources
		writer.close();
		reader.close();

		if (response == null || response.startsWith("ERR"))
		{
			throw new IOException(response); //Throw the exception
		}
		//if (response.startsWith("OK")) - We should get "OK" followed by an optional description if everything goes right
	}
	
	
	public void disable(final Plugin plugin) throws IOException
	{
		if (isOptOut() || taskId < 0) {
			return;
		}
		plugin.getServer().getScheduler().cancelTask(taskId);
		taskId = -1;
		configuration.set(OPTOUT, true);
		final File file = new File(CONFIG_FILE);
		configuration.save(file);
	}

	/**
	 * Encode text as UTF-8
	 *
	 * @param text
	 * @return
	 */
	private static String encode(final String text) throws UnsupportedEncodingException
	{
		return URLEncoder.encode(text, "UTF-8");
	}
}