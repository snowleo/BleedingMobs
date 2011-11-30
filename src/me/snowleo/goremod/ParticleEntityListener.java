/*
 * GoreMod - a blood plugin for Bukkit
 * Copyright (C) 2011  snowleo
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.snowleo.goremod;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;


class ParticleEntityListener extends EntityListener
{
	private final transient IGoreMod goreMod;

	public ParticleEntityListener(final IGoreMod goreMod)
	{
		super();
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
				goreMod.getStorage().createParticle(loc, ParticleType.CREEPER);
			}
			else if (event.getEntity() instanceof Skeleton)
			{
				goreMod.getStorage().createParticle(loc, ParticleType.SKELETON);
			}
			else if (event.getEntity() instanceof Enderman)
			{
				goreMod.getStorage().createParticle(loc, ParticleType.ENDERMAN);
			}
			else if (event.getEntity() instanceof EnderDragon)
			{
				goreMod.getStorage().createParticle(loc, ParticleType.ENDERDRAGON);
			}
			else if (entityEvent.getDamager() instanceof Projectile && event.getEntity() instanceof LivingEntity)
			{
				goreMod.getStorage().createParticle(loc, ParticleType.PROJECTILE);
			}
			else if (event.getEntity() instanceof LivingEntity)
			{
				goreMod.getStorage().createParticle(loc, ParticleType.ATTACK);
			}
		}
		if (event.getCause() == EntityDamageEvent.DamageCause.FALL
			&& event.getEntity() instanceof LivingEntity
			&& !(event.getEntity() instanceof Creeper
				 || event.getEntity() instanceof Skeleton
				 || event.getEntity() instanceof Enderman
			|| event.getEntity() instanceof EnderDragon))
		{
			final Location loc = event.getEntity().getLocation();
			goreMod.getStorage().createParticle(loc, ParticleType.FALL);
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
			goreMod.getStorage().createParticle(loc, ParticleType.CREEPER);
		}
		else if (event.getEntity() instanceof Skeleton)
		{
			goreMod.getStorage().createParticle(loc, ParticleType.SKELETON);
		}
		else if (event.getEntity() instanceof Enderman)
		{
			goreMod.getStorage().createParticle(loc, ParticleType.ENDERMAN);
		}
		else if (event.getEntity() instanceof LivingEntity)
		{
			goreMod.getStorage().createParticle(loc, ParticleType.DEATH);
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
			goreMod.getStorage().removeUnbreakableBeforeExplosion(block.getLocation());
		}
	}

	@Override
	public void onEndermanPickup(final EndermanPickupEvent event)
	{
		if (event.isCancelled() || !goreMod.isWorldEnabled(event.getBlock().getWorld()))
		{
			return;
		}
		goreMod.getStorage().removeUnbreakableBeforeExplosion(event.getBlock().getLocation());
	}
}
