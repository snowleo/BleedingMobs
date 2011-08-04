package me.snowleo.goremod;

import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;


class ParticleBlockListener extends BlockListener
{
	private final transient IGoreMod goreMod;

	public ParticleBlockListener(final IGoreMod goreMod)
	{
		this.goreMod = goreMod;
	}

	@Override
	public void onBlockBreak(final BlockBreakEvent event)
	{
		final Location loc = event.getBlock().getLocation();
		if (goreMod.isWorldEnabled(loc.getWorld()) && goreMod.isUnbreakable(loc))
		{
			event.setCancelled(true);
		}
	}
}
