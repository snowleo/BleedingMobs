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
import me.snowleo.bleedingmobs.Settings;
import org.bukkit.command.CommandSender;

class Info implements Command {
	private final IBleedingMobs plugin;

	public Info(IBleedingMobs plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void run(final CommandSender sender, final String[] args)
	{
		final Settings settings = plugin.getSettings();
		sender.sendMessage("BleedingMobs " + plugin.getDescription().getVersion() + " is " + (settings.isBleedingEnabled() ? "enabled." : "disabled."));
		sender.sendMessage("Available / max Particles: " + plugin.getStorage().getItems().getAvailableParticles() + " / " + plugin.getStorage().getItems().getMaxParticles());
		sender.sendMessage("Particles created / hour: " + plugin.getStorage().getParticleStats());
		sender.sendMessage("Active worlds: " + (settings.getWorlds().isEmpty() ? "all" : ""));
		final StringBuilder builder = new StringBuilder();
		for (String world : settings.getWorlds())
		{
			if (builder.length() != 0)
			{
				builder.append(", ");
			}
			builder.append(world);
		}
		if (builder.length() != 0)
		{
			sender.sendMessage(builder.toString());
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args)
	{
		return Collections.emptyList();
	}

}
