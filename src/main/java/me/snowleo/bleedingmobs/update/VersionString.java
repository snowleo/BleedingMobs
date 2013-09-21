package me.snowleo.bleedingmobs.update;

import java.util.regex.Pattern;


class VersionString implements Comparable<VersionString>
{
	private final String version;
	private final int[] parts;
	private static final Pattern SPLIT = Pattern.compile("[.-]");
	private static final Pattern NUMBERS = Pattern.compile("[^0-9]+");

	public VersionString(final String version)
	{
		this.version = version;
		final String[] versionParts = SPLIT.split(version);
		this.parts = new int[versionParts.length];
		for (int i = 0; i < versionParts.length; i++)
		{
			if (versionParts[i].equalsIgnoreCase("SNAPSHOT"))
			{
				this.parts[i] = -1;
			}
			else
			{
				this.parts[i] = Integer.parseInt("0" + NUMBERS.matcher(versionParts[i]).replaceAll(""));
			}
		}
	}

	@Override
	public String toString()
	{
		return version;
	}

	@Override
	public int hashCode()
	{
		return version.hashCode();
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof VersionString))
		{
			return false;
		}
		return version.equals(((VersionString)o).version);
	}

	@Override
	public int compareTo(final VersionString t)
	{
		for (int i = 0; i < Math.max(parts.length, t.parts.length); i++)
		{
			final int compare = (i >= parts.length ? 0 : parts[i]) - (i >= t.parts.length ? 0 : t.parts[i]);
			if (compare != 0)
			{
				return compare;
			}
		}
		return 0;
	}
}
