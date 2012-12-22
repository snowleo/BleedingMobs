package me.snowleo.bleedingmobs.update;

import static org.junit.Assert.*;
import org.junit.Test;


public class UpdateCheckTest
{
	public UpdateCheckTest()
	{
	}

	@Test
	public void testUpdateCheck()
	{
		final UpdateCheck check = new UpdateCheck();
		final VersionInfo info = check.checkForUpdate(new VersionString("4.0"), new VersionString("1.4.5-R0.2"));
		assertNotNull(info);
		System.out.println(info.getVersion());
		System.out.println(info.getGameVersions().iterator().next());
	}
}
