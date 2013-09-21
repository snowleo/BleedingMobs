/*
 * BleedingMobs - make your monsters and players bleed
 *
 * Copyright (C) 2011-2012 snowleo
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.snowleo.bleedingmobs.listener;

import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.particles.BleedCause;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;


public class ParticleEntityListener implements Listener
{
	private static final String BLEEDINGMOBS_BLOODSTRIKE = "bleedingmobs.bloodstrike";
	private static final String BLEEDINGMOBS_NOBLOOD = "bleedingmobs.noblood";
	private final IBleedingMobs plugin;

	public ParticleEntityListener(final IBleedingMobs plugin)
	{
		super();
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent event)
	{
		if (event.isCancelled() && !plugin.getSettings().isBleedingWhenCanceled())
		{
			return;
		}
		if (plugin.getSettings().isPermissionOnly()
			&& !(hasPlayerDamagePermission(event.getDamager())
				 || hasShooterDamagePermission(event.getDamager())))
		{
			return;
		}
		if (hasPlayerNoBloodPermission(event.getEntity()))
		{
			return;
		}
		BleedCause cause;
		if (event.getDamager() instanceof Projectile)
		{
			cause = BleedCause.PROJECTILE;
		}
		else
		{
			cause = BleedCause.ATTACK;
		}
		if (event.getEntity() instanceof LivingEntity)
		{
			plugin.getStorage().createParticles((LivingEntity)event.getEntity(), cause);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageEvent event)
	{
		if (!plugin.getSettings().isPermissionOnly()
			&& event.getCause() == EntityDamageEvent.DamageCause.FALL
			&& event.getEntity() instanceof LivingEntity
			&& !(hasPlayerNoBloodPermission(event.getEntity())))
		{
			plugin.getStorage().createParticles((LivingEntity)event.getEntity(), BleedCause.FALL);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(final EntityDeathEvent event)
	{
		plugin.getTimer().remove(event.getEntity());
		if (plugin.getSettings().isPermissionOnly()
			|| hasPlayerNoBloodPermission(event.getEntity()))
		{
			return;
		}
		plugin.getStorage().createParticles(event.getEntity(), BleedCause.DEATH);
	}

	private boolean hasPlayerDamagePermission(final Entity entity)
	{
		return entity instanceof Player
			   && ((Player)entity).hasPermission(BLEEDINGMOBS_BLOODSTRIKE);
	}

	private boolean hasShooterDamagePermission(final Entity entity)
	{
		return entity instanceof Projectile
			   && ((Projectile)entity).getShooter() instanceof Player
			   && ((Player)((Projectile)entity).getShooter()).hasPermission(BLEEDINGMOBS_BLOODSTRIKE);
	}

	private boolean hasPlayerNoBloodPermission(final Entity entity)
	{
		return entity instanceof Player
			   && ((Player)entity).hasPermission(BLEEDINGMOBS_NOBLOOD);
	}
}
