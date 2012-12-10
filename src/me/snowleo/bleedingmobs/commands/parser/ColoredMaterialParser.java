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
	private static final DyeColorParser dyeColorParser = new DyeColorParser();
	private static final MaterialParser materialParser = new MaterialParser();
	private static final List<String> validFirstValues = dyeColorParser.getValidValues();
	private static final List<String> validSecondValues = new ArrayList<String>();

	static
	{
		for (Material material : Material.values())
		{
			if (Colorable.class.isAssignableFrom(material.getData()))
			{
				validSecondValues.add(material.name().replaceAll("_", "").toLowerCase(Locale.ENGLISH));
			}
		}
	}

	@Override
	public MaterialData parse(String color, String material) throws InvalidArgumentException
	{
		DyeColor dyeColor = dyeColorParser.parse(color);
		Material coloredMaterial = materialParser.parse(material);
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
	public List<String> getValidFirstValues()
	{
		return validFirstValues;
	}

	@Override
	public List<String> getValidSecondValues(String arg1)
	{
		if (validFirstValues.contains(prepareFirstTabValue(arg1))) {
			return validSecondValues;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String prepareFirstTabValue(String arg1)
	{
		return arg1.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String prepareSecondTabValue(String arg1, String arg2)
	{
		return arg2.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
	}
}
