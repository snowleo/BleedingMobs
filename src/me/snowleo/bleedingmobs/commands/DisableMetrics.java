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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import me.snowleo.bleedingmobs.IBleedingMobs;
import org.bukkit.command.CommandSender;


class DisableMetrics implements Command
{
	private final IBleedingMobs plugin;

	public DisableMetrics(IBleedingMobs plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void run(final CommandSender sender, final String[] args)
	{
		try
		{
			if (plugin.getMetrics() != null)
			{
				plugin.getMetrics().disable();
				sender.sendMessage("Metrics disabled.");
			}
		}
		catch (IOException ex)
		{
			sender.sendMessage(ex.getMessage());
			plugin.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args)
	{
		return Collections.emptyList();
	}
}
