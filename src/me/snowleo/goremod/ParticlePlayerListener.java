package me.snowleo.goremod;

import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;


class ParticlePlayerListener extends PlayerListener
{
	private final transient IGoreMod goreMod;

	public ParticlePlayerListener(final IGoreMod goreMod)
	{
		this.goreMod = goreMod;
	}

	@Override
	public void onPlayerPickupItem(final PlayerPickupItemEvent event)
	{
		if (goreMod.isParticleItem(((CraftItem)event.getItem()).getEntityId()))
		{
			event.setCancelled(true);
		}
	}
}
