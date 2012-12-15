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
	private static final Parser<Additional> additionalParser = new EnumParser<MaterialDataParser.Additional>(Additional.class);
	private static final MaterialParser materialParser = new MaterialParser();
	private static final ColoredMaterialParser coloredMaterialParser = new ColoredMaterialParser();
	private static final TexturedMaterialParser texturedMaterialParser = new TexturedMaterialParser();
	private static final List<String> validValues = new ArrayList<String>();

	static
	{
		validValues.add(Additional.HAND.name().toLowerCase());
		validValues.add(Additional.LOOKAT.name().toLowerCase());
		validValues.addAll(materialParser.getValidValues());
		validValues.addAll(coloredMaterialParser.getValidFirstValues());
	}

	@Override
	public MaterialData parse(CommandSender sender, String[] args) throws ParserException
	{
		assertLength(args, 1);
		MaterialData mat = parseAdditional(sender, args);
		if (mat == null)
		{
			if (args.length == 2)
			{
				try
				{
					mat = coloredMaterialParser.parse(sender, args);
				}
				catch (ParserException e2)
				{
					mat = texturedMaterialParser.parse(sender, args);
				}
			}
			else
			{
				mat = materialParser.parse(sender, args).getNewData((byte)0);
			}
		}
		return mat;
	}

	@Override
	public List<String> getTabValues(String[] args)
	{
		if (args.length == 0)
		{
			return validValues;
		}
		if (args.length == 1)
		{
			return searchList(validValues, materialParser.prepareTabValue(args[0]));
		}
		List<String> validSecondValues = new ArrayList<String>();
		validSecondValues.addAll(coloredMaterialParser.getTabValues(args));
		validSecondValues.addAll(texturedMaterialParser.getTabValues(args));
		return validSecondValues;
	}

	private MaterialData parseAdditional(CommandSender sender, String[] args)
	{
		try
		{
			Additional add = additionalParser.parse(sender, args);
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
