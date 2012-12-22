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
		final VersionInfo info = check.checkForUpdate(new VersionString("3.0"), new VersionString("1.3.1-R2.0"));
		assertNotNull(info);
		System.out.println(info.getVersion());
		System.out.println(info.getGameVersions().iterator().next());
	}
}
