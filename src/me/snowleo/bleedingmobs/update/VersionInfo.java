package me.snowleo.bleedingmobs.update;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;


class VersionInfo implements Comparable<VersionInfo>
{
	private final VersionString version;
	private SortedSet<VersionString> gameVersions;

	VersionInfo(final String version)
	{
		this.version = new VersionString(version.trim());
	}

	public VersionString getVersion()
	{
		return version;
	}

	public SortedSet<VersionString> getGameVersions()
	{
		return gameVersions;
	}

	void setGameVersions(final String[] versions)
	{
		this.gameVersions = new TreeSet<VersionString>();
		for (String gVersion : versions)
		{
			this.gameVersions.add(new VersionString(gVersion.trim()));
		}
	}

	@Override
	public int hashCode()
	{
		return version.hashCode();
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof VersionInfo))
		{
			return false;
		}
		return version.equals(((VersionInfo)o).version);
	}

	@Override
	public int compareTo(final VersionInfo t)
	{
		return version.compareTo(t.version);
	}
}
