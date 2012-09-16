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
package me.snowleo.bleedingmobs;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;


class ParticleEntityListener implements Listener
{
	private static final String BLEEDINGMOBS_BLOODSTRIKE = "bleedingmobs.bloodstrike";
	private static final String BLEEDINGMOBS_NOBLOOD = "bleedingmobs.noblood";
	private final transient IBleedingMobs plugin;

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
			&& !((event.getDamager() instanceof Player
				  && ((Player)event.getDamager()).hasPermission(BLEEDINGMOBS_BLOODSTRIKE))
				 || (event.getDamager() instanceof Projectile
					 && ((Projectile)event.getDamager()).getShooter() instanceof Player
					 && ((Player)((Projectile)event.getDamager()).getShooter()).hasPermission(BLEEDINGMOBS_BLOODSTRIKE))))
		{
			return;
		}
		if ((event.getEntity() instanceof Player)
			&& ((Player)event.getEntity()).hasPermission(BLEEDINGMOBS_NOBLOOD))
		{
			return;
		}
		ParticleStorage.BleedCause cause;
		if (event.getDamager() instanceof Projectile)
		{
			cause = ParticleStorage.BleedCause.PROJECTILE;
		}
		else
		{
			cause = ParticleStorage.BleedCause.ATTACK;
		}
		if (event.getEntity() instanceof LivingEntity)
		{
			plugin.getStorage().createParticle((LivingEntity)event.getEntity(), cause);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageEvent event)
	{
		if (!plugin.getSettings().isPermissionOnly()
			&& event.getCause() == EntityDamageEvent.DamageCause.FALL
			&& event.getEntity() instanceof LivingEntity
			&& !((event.getEntity() instanceof Player)
				 && ((Player)event.getEntity()).hasPermission(BLEEDINGMOBS_NOBLOOD)))
		{
			plugin.getStorage().createParticle((LivingEntity)event.getEntity(), ParticleStorage.BleedCause.FALL);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(final EntityDeathEvent event)
	{
		plugin.getTimer().remove(event.getEntity());
		if (plugin.getSettings().isPermissionOnly()
			|| ((event.getEntity() instanceof Player)
				&& ((Player)event.getEntity()).hasPermission(BLEEDINGMOBS_NOBLOOD)))
		{
			return;
		}
		plugin.getStorage().createParticle(event.getEntity(), ParticleStorage.BleedCause.DEATH);
	}
}
