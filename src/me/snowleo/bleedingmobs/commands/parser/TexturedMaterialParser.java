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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TexturedMaterial;


public class TexturedMaterialParser extends DoubleValueParser<MaterialData>
{
	private static final MaterialParser MATERIAL_PARSER = new MaterialParser();
	private static final ListMultimap<String, String> VALID_VALUES = LinkedListMultimap.create();

	static
	{
		for (Material material : Material.values())
		{
			if (TexturedMaterial.class.isAssignableFrom(material.getData()))
			{
				TexturedMaterial data = (TexturedMaterial)material.getNewData((byte)0);

				for (Material texMat : data.getTextures())
				{
					String texName = texMat.name().replaceAll("_", "").toLowerCase(Locale.ENGLISH);
					String matName = material.name().replaceAll("_", "").toLowerCase(Locale.ENGLISH);
					VALID_VALUES.put(texName, matName);
				}
			}
		}
	}

	@Override
	protected MaterialData parse(final String texture, final String material) throws InvalidArgumentException
	{
		Material texMat = MATERIAL_PARSER.parse(texture);
		Material coloredMaterial = MATERIAL_PARSER.parse(material);
		MaterialData data = coloredMaterial.getNewData((byte)0);
		if (data instanceof TexturedMaterial && ((TexturedMaterial)data).getTextures().contains(texMat))
		{
			data.setData((byte)((TexturedMaterial)data).getTextures().indexOf(texMat));
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
		return new ArrayList<String>(VALID_VALUES.keySet());
	}

	@Override
	protected List<String> getValidSecondValues(final String arg1)
	{
		return VALID_VALUES.get(arg1);
	}

	@Override
	protected String prepareFirstTabValue(final String arg1)
	{
		return arg1.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
	}

	@Override
	protected String prepareSecondTabValue(String arg1, String arg2)
	{
		return arg2.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH);
	}
}
