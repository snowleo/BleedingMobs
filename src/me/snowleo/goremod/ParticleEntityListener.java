package me.snowleo.goremod;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;


class ParticleEntityListener extends EntityListener
{
	private final transient GoreMod goreMod;
	private final transient Random random = new Random();

	public ParticleEntityListener(final GoreMod goreMod)
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
			final int amount = Math.abs(random.nextInt()) % 10 + 15;
			for (int i = 0; i < amount; i++)
			{
				goreMod.createParticle(loc);
			}
		}
		if (event instanceof EntityDamageByProjectileEvent)
		{
			final Location loc = event.getEntity().getLocation();
			final int amount = Math.abs(random.nextInt()) % 10 + 5;
			for (int i = 0; i < amount; i++)
			{
				goreMod.createParticle(loc);
			}
		}
	}

	@Override
	public void onEntityDeath(final EntityDeathEvent event)
	{
		final Location loc = event.getEntity().getLocation();
		final int amount = Math.abs(random.nextInt()) % 10 + 25;
		for (int i = 0; i < amount; i++)
		{
			goreMod.createParticle(loc);
		}
	}
}
