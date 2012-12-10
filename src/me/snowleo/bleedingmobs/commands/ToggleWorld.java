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
package me.snowleo.bleedingmobs.commands;

import java.util.Iterator;
import java.util.Set;
import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.Settings;
import me.snowleo.bleedingmobs.commands.parser.WorldParser;
import org.bukkit.World;
import org.bukkit.command.CommandSender;


class ToggleWorld extends AbstractConfigCommand<World>
{
	public ToggleWorld(IBleedingMobs plugin)
	{
		super(plugin, new WorldParser());
	}

	@Override
	public void run(final CommandSender sender, final World value, final Settings settings)
	{
		final Set<String> worlds = settings.getWorlds();
		for (final Iterator<String> it = worlds.iterator(); it.hasNext();)
		{
			final String worldName = it.next();
			if (plugin.getServer().getWorld(worldName) == null)
			{
				it.remove();
			}
		}
		if (worlds.isEmpty())
		{
			for (World world : plugin.getServer().getWorlds())
			{
				worlds.add(world.getName());
			}
		}
		World world = value;
		if (worlds.contains(world.getName()))
		{
			worlds.remove(world.getName());
			sender.sendMessage("BleedingMobs is now disabled in world " + world.getName() + ".");
		}
		else
		{
			worlds.add(world.getName());
			sender.sendMessage("BleedingMobs is now enabled in world " + world.getName() + ".");
		}
		int activeWorlds = 0;
		for (World w : plugin.getServer().getWorlds())
		{
			if (worlds.contains(w.getName()))
			{
				activeWorlds += 1;
			}
		}
		if (activeWorlds == plugin.getServer().getWorlds().size())
		{
			worlds.clear();
		}
	}
}
