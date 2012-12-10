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
package me.snowleo.bleedingmobs.commands.parser;

import java.util.Locale;
import org.bukkit.Material;


public class MaterialParser extends EnumParser<Material>
{
	private final IntegerParser integerParser = new LowerBoundIntegerParser(0);

	public MaterialParser()
	{
		super(Material.class);
	}

	@Override
	public Material parse(String arg) throws InvalidArgumentException
	{
		Material mat;
		try
		{
			Integer value = integerParser.parse(arg);
			mat = Material.getMaterial(value);
			if (mat == null)
			{
				throw new InvalidArgumentException();
			}
		}
		catch (InvalidArgumentException e)
		{
			mat = super.parse(arg);
		}
		return mat;
	}

	@Override
	public String prepareTabValue(String arg)
	{
		return arg.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
	}
}
