package me.snowleo.bleedingmobs.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import me.snowleo.bleedingmobs.commands.parser.ParserException;
import org.bukkit.command.CommandSender;


public abstract class AbstractSubCommand implements Command
{
	private final Map<String, Command> subcommands = new HashMap<String, Command>();

	protected final void register(final String name, final Command command)
	{
		subcommands.put(name.toLowerCase(Locale.ENGLISH), command);
	}

	protected abstract String[] getInfo();

	@Override
	public void run(final CommandSender sender, final String[] args) throws ParserException
	{
		if (args.length == 0)
		{
			sender.sendMessage(getInfo());
			return;
		}
		final String commandString = args[0].toLowerCase(Locale.ENGLISH);
		final Command command = subcommands.get(commandString);
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

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args)
	{
		if (args.length == 0)
		{
			return new ArrayList<String>(subcommands.keySet());
		}
		if (args.length == 1)
		{

			List<String> values = new ArrayList<String>();
			final String commandString = args[0].toLowerCase(Locale.ENGLISH);
			for (String command : subcommands.keySet())
			{
				if (command.startsWith(commandString))
				{
					values.add(command);
				}
			}
			return values;
		}
		final String commandString = args[0].toLowerCase(Locale.ENGLISH);
		final Command command = subcommands.get(commandString);
		if (command == null)
		{
			return Collections.<String>emptyList();
		}
		else
		{
			final String[] args2 = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
			return command.tabComplete(sender, args2);
		}
	}
}
