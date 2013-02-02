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
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;


public class MaterialDataParser extends AbstractParser<MaterialData>
{
	private static final Parser<Additional> ADDITIONAL_PARSER = new EnumParser<MaterialDataParser.Additional>(Additional.class);
	private static final MaterialParser MATERIAL_PARSER = new MaterialParser();
	private static final ColoredMaterialParser COLORED_MATERIAL_PARSER = new ColoredMaterialParser();
	private static final TexturedMaterialParser TEXTURED_MATERIAL_PARSER = new TexturedMaterialParser();
	private static final List<String> VALID_VALUES = new ArrayList<String>();

	static
	{
		VALID_VALUES.add(Additional.HAND.name().toLowerCase());
		VALID_VALUES.add(Additional.LOOKAT.name().toLowerCase());
		VALID_VALUES.addAll(MATERIAL_PARSER.getValidValues());
		VALID_VALUES.addAll(COLORED_MATERIAL_PARSER.getValidFirstValues());
	}

	@Override
	public MaterialData parse(final CommandSender sender, final String[] args) throws ParserException
	{
		assertLength(args, 1);
		MaterialData mat = parseAdditional(sender, args);
		if (mat == null)
		{
			if (args.length == 2)
			{
				try
				{
					mat = COLORED_MATERIAL_PARSER.parse(sender, args);
				}
				catch (ParserException e2)
				{
					mat = TEXTURED_MATERIAL_PARSER.parse(sender, args);
				}
			}
			else
			{
				mat = MATERIAL_PARSER.parse(sender, args).getNewData((byte)0);
			}
		}
		return mat;
	}

	@Override
	public List<String> getTabValues(final String[] args)
	{
		if (args.length == 0)
		{
			return VALID_VALUES;
		}
		if (args.length == 1)
		{
			return searchList(VALID_VALUES, MATERIAL_PARSER.prepareTabValue(args[0]));
		}
		List<String> validSecondValues = new ArrayList<String>();
		validSecondValues.addAll(COLORED_MATERIAL_PARSER.getTabValues(args));
		validSecondValues.addAll(TEXTURED_MATERIAL_PARSER.getTabValues(args));
		return validSecondValues;
	}

	private MaterialData parseAdditional(final CommandSender sender, final String[] args)
	{
		try
		{
			Additional add = ADDITIONAL_PARSER.parse(sender, args);
			switch (add)
			{
			case HAND:
				return parseHand(sender);
			default:
			case LOOKAT:
				return parseLookAt(sender);
			}
		}
		catch (ParserException ex)
		{
			return null;
		}
	}

	private MaterialData parseHand(CommandSender sender)
	{
		if (sender instanceof Player)
		{
			ItemStack itemInHand = ((Player)sender).getItemInHand();
			return itemInHand == null ? null : itemInHand.getData();
		}
		return null;
	}

	private MaterialData parseLookAt(CommandSender sender)
	{

		if (sender instanceof Player)
		{
			Block block = ((Player)sender).getTargetBlock(null, 100);
			return block == null ? null : block.getType().getNewData(block.getData());
		}
		return null;
	}


	private static enum Additional
	{
		HAND, LOOKAT
	}
}
