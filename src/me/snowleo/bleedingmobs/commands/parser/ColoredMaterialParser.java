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
import java.util.List;
import java.util.Locale;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;


public class ColoredMaterialParser extends DoubleValueParser<MaterialData>
{
	private static final DyeColorParser DYE_COLOR_PARSER = new DyeColorParser();
	private static final MaterialParser MATERIAL_PARSER = new MaterialParser();
	private static final List<String> VALID_FIRST_VALUES = DYE_COLOR_PARSER.getValidValues();
	private static final List<String> VALID_SECOND_VALUES = new ArrayList<String>();

	static
	{
		for (Material material : Material.values())
		{
			if (Colorable.class.isAssignableFrom(material.getData()))
			{
				VALID_SECOND_VALUES.add(material.name().replaceAll("_", "").toLowerCase(Locale.ENGLISH));
			}
		}
	}

	@Override
	protected MaterialData parse(final String color, final String material) throws InvalidArgumentException
	{
		DyeColor dyeColor = DYE_COLOR_PARSER.parse(color);
		Material coloredMaterial = MATERIAL_PARSER.parse(material);
		MaterialData data = coloredMaterial.getNewData((byte)0);
		if (data instanceof Colorable)
		{
			data.setData(dyeColor.getData());
		}
		else
		{
			throw new InvalidArgumentException();
		}
		return data;
	}

	@Override
	protected List<String> getValidFirstValues()
	{
		return VALID_FIRST_VALUES;
	}

	@Override
	protected List<String> getValidSecondValues(final String arg1)
	{
		if (VALID_FIRST_VALUES.contains(prepareFirstTabValue(arg1)))
		{
			return VALID_SECOND_VALUES;
		}
		else
		{
			return Collections.emptyList();
		}
	}

	@Override
	protected String prepareFirstTabValue(final String arg1)
	{
		return arg1.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
	}

	@Override
	protected String prepareSecondTabValue(final String arg1, final String arg2)
	{
		return arg2.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
	}
}
