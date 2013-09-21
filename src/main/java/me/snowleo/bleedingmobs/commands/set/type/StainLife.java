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
package me.snowleo.bleedingmobs.commands.set.type;

import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.commands.AbstractTypeCommand;
import me.snowleo.bleedingmobs.commands.parser.FromToIntegerParser;
import me.snowleo.bleedingmobs.commands.parser.LowerBoundIntegerParser;
import me.snowleo.bleedingmobs.particles.ParticleType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;


class StainLife extends AbstractTypeCommand<Integer[]>
{
	StainLife(final EntityType type, final IBleedingMobs plugin)
	{
		super(type, plugin, new FromToIntegerParser(new LowerBoundIntegerParser(0)));
	}

	@Override
	protected void run(final CommandSender sender, final Integer[] arg, final ParticleType.Builder type)
	{
		final int stainlifefrom = arg[0];
		final int stainlifeto = arg[1];
		type.setStainLifeFrom(stainlifefrom);
		type.setStainLifeTo(stainlifeto);
		sender.sendMessage("Stain Life set to a random number between " + stainlifefrom + " and " + stainlifeto + ".");
	}
}