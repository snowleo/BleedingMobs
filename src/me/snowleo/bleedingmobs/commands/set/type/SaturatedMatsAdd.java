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

import java.util.Locale;
import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.commands.AbstractTypeCommand;
import me.snowleo.bleedingmobs.commands.parser.MaterialParser;
import me.snowleo.bleedingmobs.particles.ParticleType;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;


class SaturatedMatsAdd extends AbstractTypeCommand<Material>
{
	public SaturatedMatsAdd(ParticleType type, IBleedingMobs plugin)
	{
		super(type, plugin, new MaterialParser()); //TODO: Add additional
	}

	@Override
	public void run(CommandSender sender, Material mat, ParticleType type)
	{
		type.getSaturatedMaterials().add(mat);
		sender.sendMessage("Material " + mat.toString().replace('_', '-').toLowerCase(Locale.ENGLISH) + " added to saturated materials.");
	}
}
