package me.snowleo.bleedingmobs.update;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;


public class UpdateNotifier implements Listener
{
	private final Plugin plugin;
	private VersionInfo info;

	public UpdateNotifier(final Plugin plugin)
	{
		this.plugin = plugin;
	}

	public void check()
	{
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new AsyncUpdateCheck(), 0, 20 * 60 * 60 * 24);
	}


	private class AsyncUpdateCheck implements Runnable
	{
		@Override
		public void run()
		{
			final UpdateCheck updateCheck = new UpdateCheck();
			info = updateCheck.checkForUpdate(getCurrentVersion(), getBukkitVersion());
			if (info != null)
			{
				plugin.getLogger().info("New version of BleedingMobs released: " + info.getVersion());
				plugin.getLogger().info("You can download it here: http://dev.bukkit.org/server-mods/bleedingmobs/files/");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		if (info != null && player.hasPermission("bleedingmobs.update"))
		{
			player.sendMessage("New version of BleedingMobs released: " + info.getVersion());
			player.sendMessage("You can download it here: http://dev.bukkit.org/server-mods/bleedingmobs/files/");
		}
	}

	private VersionString getCurrentVersion()
	{
		return new VersionString(plugin.getDescription().getVersion());
	}

	private VersionString getBukkitVersion()
	{
		return new VersionString(plugin.getServer().getBukkitVersion());
	}
}
