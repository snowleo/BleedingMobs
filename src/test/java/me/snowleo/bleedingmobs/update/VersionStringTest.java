package me.snowleo.bleedingmobs.update;

import static org.junit.Assert.*;
import org.junit.Test;


public class VersionStringTest
{
	public VersionStringTest()
	{
	}

	@Test
	public void testCompare()
	{
		final VersionString a = new VersionString("1.0");
		final VersionString b = new VersionString("1.0.1");
		final VersionString c = new VersionString("1.0-SNAPSHOT");
		assertTrue(a.compareTo(b) < 0);
		assertTrue(a.compareTo(c) > 0);
		assertTrue(b.compareTo(c) > 0);
	}
}
