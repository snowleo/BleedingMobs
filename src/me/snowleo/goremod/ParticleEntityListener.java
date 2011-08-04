package me.snowleo.goremod;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;


class ParticleEntityListener extends EntityListener
{
	private final transient IGoreMod goreMod;

	public ParticleEntityListener(final IGoreMod goreMod)
	{
		this.goreMod = goreMod;
	}

	@Override
	public void onEntityDamage(final EntityDamageEvent event)
	{
		if (event instanceof EntityDamageByEntityEvent)
		{
			final EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent)event;
			final Location loc = entityEvent.getEntity().getLocation();
			if (!goreMod.isWorldEnabled(loc.getWorld()))
			{
				return;
			}
			if (event.getEntity() instanceof Creeper)
			{
				goreMod.createParticle(loc, ParticleType.CREEPER);
			}
			else
			{
				goreMod.createParticle(loc, ParticleType.ATTACK);
			}
		}
		if (event instanceof EntityDamageByProjectileEvent)
		{
			final Location loc = event.getEntity().getLocation();
			if (!goreMod.isWorldEnabled(loc.getWorld()))
			{
				return;
			}
			if (event.getEntity() instanceof Creeper)
			{
				goreMod.createParticle(loc, ParticleType.CREEPER);
			}
			else
			{
				goreMod.createParticle(loc, ParticleType.PROJECTILE);
			}
		}
	}

	@Override
	public void onEntityDeath(final EntityDeathEvent event)
	{
		final Location loc = event.getEntity().getLocation();
		if (!goreMod.isWorldEnabled(loc.getWorld()))
		{
			return;
		}
		if (event.getEntity() instanceof Creeper)
		{
			goreMod.createParticle(loc, ParticleType.CREEPER);
		}
		else
		{
			goreMod.createParticle(loc, ParticleType.DEATH);
		}
	}

	@Override
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		if (event.isCancelled() || !goreMod.isWorldEnabled(event.getLocation().getWorld()))
		{
			return;
		}
		for (Block block : event.blockList())
		{
			if (goreMod.removeUnbreakable(block.getLocation()))
			{
				event.setYield(0.0f);
			}
		}
	}
}
