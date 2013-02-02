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
	private static final Map<String, DyeColor> COLOR_MAP = new HashMap<String, DyeColor>();
	private static final BoundedIntegerParser INTEGER_PARSER = new BoundedIntegerParser(0, 15);
	private static final List<String> VALID_VALUES = new ArrayList<String>();

	static
	{
		for (DyeColor dyeColor : DyeColor.values())
		{
			String colorName = dyeColor.toString().replace("_", "").toLowerCase(Locale.ENGLISH);
			COLOR_MAP.put(colorName, dyeColor);
			VALID_VALUES.add(colorName);
		}
		Collections.sort(VALID_VALUES);
	}

	@Override
	protected DyeColor parse(final String arg) throws InvalidArgumentException
	{

		final String colorName = arg.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
		DyeColor dyecolor = COLOR_MAP.get(colorName);
		if (dyecolor == null)
		{
			Integer parse = INTEGER_PARSER.parse(arg);
			dyecolor = DyeColor.getByWoolData(parse.byteValue());
		}
		return dyecolor;
	}

	@Override
	protected List<String> getValidValues()
	{
		return VALID_VALUES;
	}

	@Override
	protected String prepareTabValue(final String arg)
	{
		return arg.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
	}
}
