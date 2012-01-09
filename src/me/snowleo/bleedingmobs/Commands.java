/*
 * BleedingMobs - make your monsters and players bleed
 *
 * Copyright (C) 2011 snowleo
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
package me.snowleo.bleedingmobs;

import java.util.*;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commands
{
	private transient final IBleedingMobs plugin;
	private transient final Map<String, Command> allcommands = new HashMap<String, Command>();

	public Commands(final IBleedingMobs plugin)
	{
		this.plugin = plugin;
		allcommands.put("toggle", new Toggle());
		allcommands.put("reload", new Reload());
		allcommands.put("info", new Info());
		allcommands.put("toggle-world", new ToggleWorld());
		allcommands.put("set", new SetCommand());
		allcommands.put("set-maxparticles", new MaxParticles());
		allcommands.put("set-bleedwhencanceled", new BleedWhenCanceled());
		for (ParticleType particleType : ParticleType.values())
		{
			allcommands.put("set-" + particleType.toString().toLowerCase(Locale.ENGLISH), new TypeCommand(particleType));
		}
	}

	public void run(final CommandSender sender, final String[] args)
	{
		final String commandString = args[0].toLowerCase(Locale.ENGLISH);
		final Command command = allcommands.get(commandString);
		if (command == null)
		{
			sender.sendMessage("Command not found.");
		}
		else
		{
			final String[] args2 = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
			command.run(sender, args2);
		}
	}


	private interface Command
	{
		void run(CommandSender sender, String[] args);
	}


	private abstract class AbstractConfigCommand implements Command
	{
		public abstract void changeConfig(CommandSender sender, String[] args, Settings settings);

		@Override
		public void run(final CommandSender sender, final String[] args)
		{
			plugin.getSettings().loadConfig();
			changeConfig(sender, args, plugin.getSettings());
			plugin.getSettings().saveConfig();
		}
	}


	private class Toggle extends AbstractConfigCommand
	{
		@Override
		public void changeConfig(final CommandSender sender, final String[] args, final Settings settings)
		{
			settings.setBleedingEnabled(!settings.isBleedingEnabled());
			sender.sendMessage("BleedingMobs is now " + (settings.isBleedingEnabled() ? "enabled." : "disabled."));
		}
	}


	private class Reload extends AbstractConfigCommand
	{
		@Override
		public void changeConfig(final CommandSender sender, final String[] args, final Settings settings)
		{
			sender.sendMessage("BleedingMobs " + plugin.getDescription().getVersion() + " reloaded.");
		}
	}


	private class Info implements Command
	{
		@Override
		public void run(final CommandSender sender, final String[] args)
		{
			final Settings settings = plugin.getSettings();
			sender.sendMessage("BleedingMobs " + plugin.getDescription().getVersion() + " is " + (settings.isBleedingEnabled() ? "enabled." : "disabled."));
			sender.sendMessage("Max Particles (cache): " + settings.getMaxParticles() + " (" + plugin.getStorage().getCacheSize() + ")");
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
	}


	private class ToggleWorld extends AbstractConfigCommand
	{
		@Override
		public void changeConfig(final CommandSender sender, final String[] args, final Settings settings)
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
			World world = null;
			if (args.length > 0)
			{
				world = plugin.getServer().getWorld(args[0]);
			}
			else if (sender instanceof Player)
			{
				world = ((Player)sender).getWorld();
			}

			if (world == null)
			{
				sender.sendMessage("World not found.");
				return;
			}

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


	private class SetCommand implements Command
	{
		@Override
		public void run(final CommandSender sender, final String[] args)
		{
			if (args.length == 0)
			{
				sender.sendMessage("Available Subcommands:");
				sender.sendMessage("set maxparticles [num], set bleedwhencanceled, set [type] woolchance [num], set [type] bonechance [num], set [type] woolcolor [color],");
				sender.sendMessage("set [type] stainsfloor (true|false), set [type] bonelife [num], set [type] particlematerial (material|hand|lookat), set [type] saturatedmats clear,");
				sender.sendMessage("set [type] particlelife [num] [num], set [type] stainlife [num] [num], set [type] amount [num] [num], set [type] saturatedmats (add|remove) (material|hand|lookat)");
				sender.sendMessage("Available types:");
				final StringBuilder builder = new StringBuilder();
				for (ParticleType particleType : ParticleType.values())
				{
					if (builder.length() != 0)
					{
						builder.append(", ");
					}
					builder.append(particleType.toString().toLowerCase(Locale.ENGLISH));
				}
				sender.sendMessage(builder.toString());
				return;
			}
			final String commandString = args[0].toLowerCase(Locale.ENGLISH);
			final Command command = allcommands.get("set-" + commandString);
			if (command == null)
			{
				sender.sendMessage("Command not found.");
			}
			else
			{
				final String[] args2 = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
				command.run(sender, args2);
			}
		}
	}


	private class MaxParticles extends AbstractConfigCommand
	{
		@Override
		public void changeConfig(final CommandSender sender, final String[] args, final Settings settings)
		{
			if (args.length == 0)
			{
				sender.sendMessage("You have to set the number.");
				return;
			}
			final int newMaxParticles = Integer.parseInt(args[0]);
			plugin.getStorage().changeMaxParticles(newMaxParticles - settings.getMaxParticles());
			settings.setMaxParticles(newMaxParticles);
		}
	}
	
	private class BleedWhenCanceled extends AbstractConfigCommand
	{
		@Override
		public void changeConfig(final CommandSender sender, final String[] args, final Settings settings)
		{
			settings.setBleedWhenCanceled(!settings.isBleedingWhenCanceled());
			sender.sendMessage("Bleed when cancelled set to " + (settings.isBleedingWhenCanceled() ? "true" : "false") + ".");
		}
	}


	private class TypeCommand extends AbstractConfigCommand
	{
		private transient final ParticleType type;

		public TypeCommand(final ParticleType particleType)
		{
			super();
			this.type = particleType;
		}

		@Override
		public void changeConfig(final CommandSender sender, final String[] args, final Settings settings)
		{
			if (args.length < 2)
			{
				sender.sendMessage("Not enough arguments.");
				return;
			}
			final String key = args[0].toLowerCase(Locale.ENGLISH).replace("-", "");

			if ("woolchance".equals(key))
			{
				final int woolchance = Math.min(100, Math.max(0, Integer.parseInt(args[1])));
				type.setWoolChance(woolchance);
				sender.sendMessage("Wool chance set to " + woolchance + "%.");
			}
			else if ("bonechance".equals(key))
			{
				final int bonechance = Math.min(100, Math.max(0, Integer.parseInt(args[1])));
				type.setBoneChance(bonechance);
				sender.sendMessage("Bone chance set to " + bonechance + "%.");
			}
			else if ("woolcolor".equals(key))
			{
				final String colorName = args[1].replaceAll("[_-]", "").toUpperCase(Locale.ENGLISH);
				byte woolcolor = -1;
				for (DyeColor dyeColor : DyeColor.values())
				{
					if (dyeColor.toString().replace("_", "").equals(colorName))
					{
						woolcolor = dyeColor.getData();
					}
				}
				if (woolcolor < 0)
				{
					woolcolor = (byte)Math.min(15, Math.max(0, Integer.parseInt(args[1])));
				}
				type.setWoolColor(DyeColor.getByData(woolcolor));
				sender.sendMessage("Wool color set to " + DyeColor.getByData(woolcolor).toString().replace('_', '-').toLowerCase(Locale.ENGLISH) + ".");
			}
			else if ("stainsfloor".equals(key))
			{
				if ("true".equalsIgnoreCase(args[1]) || "on".equalsIgnoreCase(args[1]))
				{
					type.setStainsFloor(true);
				}
				else
				{
					type.setStainsFloor(false);
				}
				sender.sendMessage("Stains floor set to " + (type.isStainingFloor() ? "true" : "false") + ".");
			}
			else if ("bonelife".equals(key))
			{
				final int bonelife = Math.max(0, Integer.parseInt(args[1]));
				type.setBoneLife(bonelife);
				sender.sendMessage("Bone life set to " + bonelife + " ticks.");
			}
			else if ("particlematerial".equals(key))
			{
				final Material mat = parseMaterial(args[1], sender);
				if (mat != null)
				{
					type.setParticleMaterial(mat);
					sender.sendMessage("Particle material set to " + mat.toString().replace('_', '-').toLowerCase(Locale.ENGLISH) + ".");
				}
			}
			else if ("saturatedmats".equals(key) && args[1].equalsIgnoreCase("clear"))
			{
				type.setSaturatedMaterials(EnumSet.noneOf(Material.class));
			}
			else if (args.length < 3)
			{
				sender.sendMessage("Not enough arguments.");
			}
			else if ("particlelife".equals(key))
			{
				final int particlelifefrom = Math.max(0, Integer.parseInt(args[1]));
				final int particlelifeto = Math.max(particlelifefrom, Integer.parseInt(args[2]));

				type.setParticleLifeFrom(particlelifefrom);
				type.setParticleLifeTo(particlelifeto);
				sender.sendMessage("Particle Life set to a random number between " + particlelifefrom + " and " + particlelifeto + ".");
			}
			else if ("stainlife".equals(key))
			{
				final int stainlifefrom = Math.max(0, Integer.parseInt(args[1]));
				final int stainlifeto = Math.max(stainlifefrom, Integer.parseInt(args[2]));

				type.setStainLifeFrom(stainlifefrom);
				type.setStainLifeTo(stainlifeto);
				sender.sendMessage("Stain Life set to a random number between " + stainlifefrom + " and " + stainlifeto + ".");
			}
			else if ("amount".equals(key))
			{
				final int amountfrom = Math.max(0, Integer.parseInt(args[1]));
				final int amountto = Math.max(amountfrom, Integer.parseInt(args[2]));

				type.setAmountFrom(amountfrom);
				type.setAmountTo(amountto);
				sender.sendMessage("Amount set to a random number between " + amountfrom + " and " + amountto + ".");
			}
			else if ("saturatedmats".equals(key) && args[1].equalsIgnoreCase("add"))
			{
				final Material mat = parseMaterial(args[2], sender);
				if (mat != null)
				{
					type.getSaturatedMaterials().add(mat);
					sender.sendMessage("Material " + mat.toString().replace('_', '-').toLowerCase(Locale.ENGLISH) + " added to saturated materials.");
				}
			}
			else if ("saturatedmats".equals(key) && args[1].equalsIgnoreCase("remove"))
			{
				final Material mat = parseMaterial(args[2], sender);
				if (mat != null)
				{
					type.getSaturatedMaterials().remove(mat);
					sender.sendMessage("Material " + mat.toString().replace('_', '-').toLowerCase(Locale.ENGLISH) + " removed from saturated materials.");
				}
			}
		}

		private Material parseMaterial(final String arg, final CommandSender sender) throws NumberFormatException
		{
			final String materialName = arg.replaceAll("[_-]", "").toUpperCase(Locale.ENGLISH);
			Material mat = null;
			if ("HAND".equals(materialName) && sender instanceof Player
				&& ((Player)sender).getItemInHand() != null)
			{
				mat = ((Player)sender).getItemInHand().getType();
			}
			if ("LOOKAT".equals(materialName) && sender instanceof Player
				&& ((Player)sender).getTargetBlock(null, 100) != null)
			{
				mat = ((Player)sender).getTargetBlock(null, 100).getType();
			}
			for (Material material : Material.values())
			{
				if (material.toString().replace("_", "").equals(materialName))
				{
					mat = material;
				}
			}
			if (mat == null)
			{
				mat = Material.getMaterial(Integer.parseInt(arg));
			}
			return mat;
		}
	}
}
