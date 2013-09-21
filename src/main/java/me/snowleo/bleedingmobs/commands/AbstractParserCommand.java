package me.snowleo.bleedingmobs.commands;

import java.util.List;
import me.snowleo.bleedingmobs.commands.parser.InvalidArgumentException;
import me.snowleo.bleedingmobs.commands.parser.Parser;
import me.snowleo.bleedingmobs.commands.parser.ParserException;
import org.bukkit.command.CommandSender;


public abstract class AbstractParserCommand<T> implements Command
{
	private final Parser<T> parser;

	protected AbstractParserCommand(final Parser<T> parser)
	{
		this.parser = parser;
	}

	@Override
	public final void run(final CommandSender sender, final String[] args) throws ParserException
	{
		run(sender, parser.parse(sender, args));
	}

	protected abstract void run(final CommandSender sender, final T arg) throws InvalidArgumentException;

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args)
	{
		return parser.getTabValues(args);
	}
}
