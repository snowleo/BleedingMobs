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
import org.bukkit.permissions.Permission;


class ParticleEntityListener implements Listener
{
	private final transient IBleedingMobs plugin;
	private final transient Permission bloodstrike;

	public ParticleEntityListener(final IBleedingMobs plugin)
	{
		super();
		this.plugin = plugin;
		this.bloodstrike = plugin.getServer().getPluginManager().getPermission("bleedingmobs.bloodstrike");
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
				  && ((Player)event.getDamager()).hasPermission(bloodstrike))
				 || (event.getDamager() instanceof Projectile
					 && ((Projectile)event.getDamager()).getShooter() instanceof Player
					 && ((Player)((Projectile)event.getDamager()).getShooter()).hasPermission(bloodstrike))))
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
			&& event.getEntity() instanceof LivingEntity)
		{
			plugin.getStorage().createParticle((LivingEntity)event.getEntity(), ParticleStorage.BleedCause.FALL);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(final EntityDeathEvent event)
	{
		plugin.getTimer().remove(event.getEntity());
		if (plugin.getSettings().isPermissionOnly())
		{
			return;
		}
		plugin.getStorage().createParticle(event.getEntity(), ParticleStorage.BleedCause.DEATH);
	}
}
