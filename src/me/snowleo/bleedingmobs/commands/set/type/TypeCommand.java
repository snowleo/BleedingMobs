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
import me.snowleo.bleedingmobs.commands.AbstractSubCommand;
import me.snowleo.bleedingmobs.particles.ParticleType;


public class TypeCommand extends AbstractSubCommand
{
	public TypeCommand(final IBleedingMobs plugin, final ParticleType type)
	{
		super();
		register("amount", new Amount(type, plugin));
		register("bonechance", new BoneChance(type, plugin));
		register("bonelife", new BoneLife(type, plugin));
		register("particlelife", new ParticleLife(type, plugin));
		register("particlematerial", new ParticleMaterial(type, plugin));
		register("saturatedmats", new SaturatedMats(type, plugin));
		register("stainlife", new StainLife(type, plugin));
		register("stainsfloor", new StainsFloor(type, plugin));
		register("woolchance", new WoolChance(type, plugin));
		register("woolcolor", new WoolColor(type, plugin));
	}

	@Override
	public String[] getInfo()
	{
		String[] info = new String[]
		{
			"Available Subcommands:",
			"set [type] woolchance [num], set [type] bonechance [num], set [type] woolcolor [color], set [type] stainsfloor (true|false),",
			"set [type] bonelife [num], set [type] particlematerial (material|hand|lookat), set [type] saturatedmats clear,",
			"set [type] particlelife [num] [num], set [type] stainlife [num] [num], set [type] amount [num] [num], set [type] saturatedmats (add|remove) (material|hand|lookat)"
		};
		return info;
	}
}
