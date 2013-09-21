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
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class WorldParser extends SingleTabParser<World>
{
	@Override
	public World parse(final CommandSender sender, final String[] args) throws ParserException
	{
		if (args.length > 0)
		{
			World world = Bukkit.getWorld(args[0]);
			if (world == null)
			{
				throw new InvalidArgumentException();
			}
			else
			{
				return world;
			}
		}
		if (sender instanceof Player)
		{
			return ((Player)sender).getWorld();
		}
		throw new NotEnoughArgumentsException();
	}

	@Override
	protected List<String> getValidValues()
	{
		List<String> validValues = new ArrayList<String>();
		for (World world : Bukkit.getWorlds())
		{
			validValues.add(world.getName().toLowerCase(Locale.ENGLISH));
		}
		return validValues;
	}

	@Override
	protected String prepareTabValue(final String arg)
	{
		return arg.toLowerCase(Locale.ENGLISH);
	}
}
