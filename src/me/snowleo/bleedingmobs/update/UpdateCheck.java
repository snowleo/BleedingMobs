package me.snowleo.bleedingmobs.update;

import com.google.common.io.Closeables;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class UpdateCheck
{
	private static final String UPDATE_URL = "http://dev.bukkit.org/server-mods/bleedingmobs/files/";
	private static final Logger LOGGER = Logger.getLogger(UpdateCheck.class.getName());

	VersionInfo checkForUpdate(final VersionString currentVersion, final VersionString currentGame)
	{
		VersionInfo info = null;
		try
		{
			final List<String> page = getUpdatePage();
			final Set<VersionInfo> versions = parseVersions(page);
			for (final VersionInfo versionInfo : versions)
			{
				if (versionInfo.getVersion().compareTo(currentVersion) > 0)
				{
					for (final VersionString gameVersion : versionInfo.getGameVersions())
					{
						if (gameVersion.compareTo(currentGame) == 0)
						{
							info = versionInfo;
						}
					}
				}
				if (versionInfo.getVersion().compareTo(currentVersion) == 0
					&& (versionInfo.getGameVersions().first().compareTo(currentGame) > 0
						|| versionInfo.getGameVersions().last().compareTo(currentGame) < 0))
				{
					LOGGER.log(Level.WARNING, "BleedingMobs running on an unsupported Bukkit version. Please update!");
				}
			}
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.WARNING, "BleedingMobs failed to get version info from dev.bukkit.org");
		}
		return info;
	}

	private List<String> getUpdatePage() throws IOException
	{
		final URL url = new URL(UPDATE_URL);

		final URLConnection connection = url.openConnection();
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		connection.connect();

		final InputStream inputStream = connection.getInputStream();
		try
		{
			final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			try
			{
				final BufferedReader reader = new BufferedReader(inputStreamReader);
				try
				{
					final List<String> lines = new ArrayList<String>();
					while (true)
					{
						final String line = reader.readLine();
						if (line == null)
						{
							break;
						}
						else if (shouldKeepLine(line))
						{
							lines.add(line);
						}
					}
					return lines;
				}
				finally
				{
					Closeables.closeQuietly(reader);
				}
			}
			finally
			{
				Closeables.closeQuietly(inputStreamReader);
			}
		}
		finally
		{
			Closeables.closeQuietly(inputStream);
		}
	}

	private boolean shouldKeepLine(final String line)
	{
		return line.contains("td class=\"col-file\"") || line.contains("td class=\"col-game-version");
	}
	private static final Pattern VERSION_PATTERN = Pattern.compile(".*col-file.*>v([^<]*)</a>.*");
	private static final Pattern GAME_VERSION_PATTERN = Pattern.compile(".*col-game-version.*\"><li>(.*)</li></.*");
	private static final Pattern GAME_VERSION_SPLIT = Pattern.compile("</li><li>");

	private Set<VersionInfo> parseVersions(final List<String> lines)
	{
		final Set<VersionInfo> versions = new TreeSet<VersionInfo>();
		VersionInfo currentVersion = null;
		for (final String line : lines)
		{
			Matcher versionMatcher = VERSION_PATTERN.matcher(line);
			if (versionMatcher.matches())
			{
				currentVersion = new VersionInfo(versionMatcher.group(1));
			}
			Matcher gameVersionMatcher = GAME_VERSION_PATTERN.matcher(line);
			if (currentVersion != null && gameVersionMatcher.matches())
			{
				currentVersion.setGameVersions(GAME_VERSION_SPLIT.split(gameVersionMatcher.group(1)));
				versions.add(currentVersion);
				currentVersion = null;
			}
		}
		return versions;
	}
}
