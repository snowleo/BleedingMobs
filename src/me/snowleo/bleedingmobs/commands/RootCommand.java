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

import java.util.Collections;
import java.util.List;
import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.commands.parser.ParserException;
import me.snowleo.bleedingmobs.commands.set.SetCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;


public class RootCommand extends AbstractSubCommand implements TabCompleter, CommandExecutor
{
	public RootCommand(final IBleedingMobs plugin)
	{
		super();
		register("toggle", new Toggle(plugin));
		register("reload", new Reload(plugin));
		register("info", new Info(plugin));
		register("disable-metrics", new DisableMetrics(plugin));
		register("toggle-world", new ToggleWorld(plugin));
		register("set", new SetCommand(plugin));
	}

	@Override
	public String[] getInfo()
	{
		String[] info = new String[]
		{
			"Available Subcommands:",
			"toggle, reload, info, disable-metrics, toggle-world [name], set ...",
		};
		return info;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String string, final String[] strings)
	{
		if (strings.length < 1)
		{
			sender.sendMessage(command.getDescription());
			sender.sendMessage(command.getUsage());
		}
		else if (sender.hasPermission("bleedingmobs.admin"))
		{
			try
			{
				run(sender, strings);
			}
			catch (ParserException ex)
			{
				sender.sendMessage(ex.getMessage());
			}
		}
		else
		{
			sender.sendMessage("You need the permission bleedingmobs.admin to run this command.");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmnd, final String string, final String[] strings)
	{
		if (sender.hasPermission("bleedingmobs.admin"))
		{
			return tabComplete(sender, strings);
		}
		else
		{
			return Collections.emptyList();
		}
	}
}
