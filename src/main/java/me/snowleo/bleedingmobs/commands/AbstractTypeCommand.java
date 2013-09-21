package me.snowleo.bleedingmobs.commands;

import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.Settings;
import me.snowleo.bleedingmobs.commands.parser.InvalidArgumentException;
import me.snowleo.bleedingmobs.commands.parser.Parser;
import me.snowleo.bleedingmobs.particles.ParticleType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;


public abstract class AbstractTypeCommand<T> extends AbstractConfigCommand<T>
{
	private final EntityType type;

	protected AbstractTypeCommand(final EntityType type, final IBleedingMobs plugin, final Parser<T> parser)
	{
		super(plugin, parser);
		this.type = type;
	}

	@Override
	public final void run(final CommandSender sender, final T args, final Settings settings) throws InvalidArgumentException
	{
		ParticleType.Builder builder = ParticleType.getBuilder(type);
		run(sender, args, builder);
		ParticleType.save(builder.build());
	}

	protected abstract void run(final CommandSender sender, final T arg, final ParticleType.Builder type);
}
