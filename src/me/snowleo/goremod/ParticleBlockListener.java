package me.snowleo.goremod;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;


class ParticleBlockListener extends BlockListener
{
	private final transient GoreMod goreMod;

	public ParticleBlockListener(final GoreMod goreMod)
	{
		this.goreMod = goreMod;
	}

	@Override
	public void onBlockBreak(final BlockBreakEvent event)
	{
		if (goreMod.isUnbreakable(event.getBlock().getLocation()))
		{
			event.setCancelled(true);
		}
	}
}
