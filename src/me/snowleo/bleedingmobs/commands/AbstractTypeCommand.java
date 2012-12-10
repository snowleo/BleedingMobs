package me.snowleo.bleedingmobs.commands;

import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.Settings;
import me.snowleo.bleedingmobs.commands.parser.InvalidArgumentException;
import me.snowleo.bleedingmobs.commands.parser.Parser;
import me.snowleo.bleedingmobs.particles.ParticleType;
import org.bukkit.command.CommandSender;


public abstract class AbstractTypeCommand<T> extends AbstractConfigCommand<T>
{
	private final ParticleType type;

	public AbstractTypeCommand(ParticleType type, IBleedingMobs plugin, Parser<T> parser)
	{
		super(plugin, parser);
		this.type = type;
	}

	@Override
	public final void run(CommandSender sender, T args, Settings settings) throws InvalidArgumentException
	{
		run(sender, args, type);
	}

	public abstract void run(CommandSender sender, T arg, ParticleType type);
}
