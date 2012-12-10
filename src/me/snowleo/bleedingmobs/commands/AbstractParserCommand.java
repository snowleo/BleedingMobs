package me.snowleo.bleedingmobs.commands;

import java.util.List;
import me.snowleo.bleedingmobs.commands.parser.InvalidArgumentException;
import me.snowleo.bleedingmobs.commands.parser.Parser;
import me.snowleo.bleedingmobs.commands.parser.ParserException;
import org.bukkit.command.CommandSender;


public abstract class AbstractParserCommand<T> implements Command
{
	protected final Parser<T> parser;

	public AbstractParserCommand(Parser<T> parser)
	{
		this.parser = parser;
	}

	@Override
	public final void run(CommandSender sender, String[] args) throws ParserException
	{
		run(sender, parser.parse(sender, args));
	}

	public abstract void run(CommandSender sender, T arg) throws InvalidArgumentException;

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args)
	{
		return parser.getTabValues(args);
	}
}
