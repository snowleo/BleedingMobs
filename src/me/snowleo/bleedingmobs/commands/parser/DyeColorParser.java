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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.DyeColor;


public class DyeColorParser extends SingleValueParser<DyeColor>
{
	private static final Map<String, DyeColor> colorMap = new HashMap<String, DyeColor>();
	private static final BoundedIntegerParser integerParser = new BoundedIntegerParser(0, 15);
	private static final List<String> validValues = new ArrayList<String>();

	static
	{
		for (DyeColor dyeColor : DyeColor.values())
		{
			String colorName = dyeColor.toString().replace("_", "").toLowerCase(Locale.ENGLISH);
			colorMap.put(colorName, dyeColor);
			validValues.add(colorName);
		}
		Collections.sort(validValues);
	}

	@Override
	public DyeColor parse(String arg) throws InvalidArgumentException
	{

		final String colorName = arg.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
		DyeColor dyecolor = colorMap.get(colorName);
		if (dyecolor == null)
		{
			Integer parse = integerParser.parse(arg);
			dyecolor = DyeColor.getByData(parse.byteValue());
		}
		return dyecolor;
	}

	@Override
	public List<String> getValidValues()
	{
		return validValues;
	}

	@Override
	public String prepareTabValue(String arg)
	{
		return arg.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
	}
}
