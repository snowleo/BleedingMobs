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
import java.util.Set;
import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;


class Info implements Command
{
	private final IBleedingMobs plugin;

	Info(final IBleedingMobs plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void run(final CommandSender sender, final String[] args)
	{
		final Settings settings = plugin.getSettings();
		final StringBuilder builder = new StringBuilder();
		final Set<String> worlds = settings.getWorlds();
		for (String world : worlds)
		{
			if (builder.length() != 0)
			{
				builder.append(", ");
			}
			builder.append(world);
		}

		final int worldCount = worlds.size() > 0 ? worlds.size() : Bukkit.getWorlds().size();
		final int maxParticles = worldCount * plugin.getStorage().getItems().getLimit();
		final int availableParticles = maxParticles - plugin.getStorage().getItems().getCurrentParticleAmount();
		sender.sendMessage("BleedingMobs " + plugin.getDescription().getVersion() + " is " + (settings.isBleedingEnabled() ? "enabled." : "disabled."));
		sender.sendMessage("Available / max Particles: " + availableParticles + " / " + maxParticles);
		sender.sendMessage("Particles created / hour: " + plugin.getStorage().getParticleStats());
		sender.sendMessage("Active worlds: " + (settings.getWorlds().isEmpty() ? "all" : ""));
		if (builder.length() != 0)
		{
			sender.sendMessage(builder.toString());
		}
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args)
	{
		return Collections.emptyList();
	}
}
