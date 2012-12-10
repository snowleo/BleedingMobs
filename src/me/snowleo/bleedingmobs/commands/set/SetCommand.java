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
package me.snowleo.bleedingmobs.commands.set;

import java.util.Locale;
import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.commands.AbstractSubCommand;
import me.snowleo.bleedingmobs.commands.set.type.TypeCommand;
import me.snowleo.bleedingmobs.particles.ParticleType;


public class SetCommand extends AbstractSubCommand
{
	public SetCommand(IBleedingMobs plugin)
	{
		super();
		register("maxparticles", new MaxParticles(plugin));
		register("attackpercentage", new AttackPercentage(plugin));
		register("fallpercentage", new FallPercentage(plugin));
		register("deathpercentage", new DeathPercentage(plugin));
		register("projectilepercentage", new ProjectilePercentage(plugin));
		register("bloodstreampercentage", new BloodstreamPercentage(plugin));
		register("bloodstreaminterval", new BloodstreamInterval(plugin));
		register("bloodstreamtime", new BloodstreamTime(plugin));
		register("bleedwhencanceled", new BleedWhenCanceled(plugin));
		for (ParticleType particleType : ParticleType.values())
		{
			register(particleType.toString(), new TypeCommand(plugin, particleType));
		}
	}

	@Override
	public String[] getInfo()
	{
		final StringBuilder builder = new StringBuilder();
		for (ParticleType particleType : ParticleType.values())
		{
			if (builder.length() != 0)
			{
				builder.append(", ");
			}
			builder.append(particleType.toString().toLowerCase(Locale.ENGLISH));
		}
		String[] info = new String[]
		{
			"Available Subcommands:",
			"set maxparticles [num], set bleedwhencanceled, set attackpercentage [num], set fallpercentage [num],",
			"set deathpercentage [num], set projectilepercentage [num], set bloodstreampercentage [num],",
			"set bloodstreaminterval [num], set bloodstreamtime [num]",
			"set [type] ...",
			"Available types:",
			builder.toString()
		};
		return info;
	}
}
