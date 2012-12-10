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
package me.snowleo.bleedingmobs.particles;

import me.snowleo.bleedingmobs.Settings;


public enum BleedCause
{
	ATTACK, DEATH, PROJECTILE, FALL, BLOODSTREAM;

	public int getPercentages(Settings settings)
	{
		switch (this)
		{
		case BLOODSTREAM:
			return settings.getBloodstreamPercentage();
		case DEATH:
			return settings.getDeathPercentage();
		case FALL:
			return settings.getFallPercentage();
		case PROJECTILE:
			return settings.getProjectilePercentage();
		case ATTACK:
		default:
			return settings.getAttackPercentage();
		}
	}

	public boolean dropBones()
	{
		switch (this)
		{
		case DEATH:
			return true;
		case BLOODSTREAM:
		case FALL:
		case PROJECTILE:
		case ATTACK:
		default:
			return false;
		}
	}
}
