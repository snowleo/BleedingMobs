package me.snowleo.bleedingmobs.tasks;

import me.snowleo.bleedingmobs.IBleedingMobs;
import me.snowleo.bleedingmobs.particles.BleedCause;
import me.snowleo.bleedingmobs.particles.BloodStain;
import me.snowleo.bleedingmobs.particles.Particle;
import me.snowleo.bleedingmobs.particles.ParticleType;
import me.snowleo.bleedingmobs.particles.Util;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;


public class ParticleStateTask implements Runnable
{
	private enum State
	{
		INIT, SPAWNED, STAIN, INVALID
	}
	private final ParticleType type;
	private final IBleedingMobs plugin;
	private final BukkitScheduler scheduler;
	private final Location loc;
	private final BleedCause cause;
	private volatile BukkitTask task;
	private volatile State state;
	private volatile Particle particle;
	private volatile BloodStain bloodStain;

	public ParticleStateTask(final IBleedingMobs plugin, final ParticleType type, final Location loc, final BleedCause cause)
	{
		this.type = type;
		this.plugin = plugin;
		this.loc = loc;
		this.cause = cause;
		this.scheduler = plugin.getServer().getScheduler();
	}

	public synchronized void start()
	{
		changeState(State.INIT, Util.getRandomBetween(0, 3));
	}

	private synchronized void changeState(final State state, final int ticks)
	{
		this.state = state;
		this.task = scheduler.runTaskLater(plugin, this, ticks);
	}

	@Override
	public synchronized void run()
	{
		switch (state)
		{
		case INIT:
			particle = new Particle(plugin, loc, type, cause.dropBones());
			plugin.getStorage().getItems().add(particle.getItem(), this);
			changeState(State.SPAWNED, particle.getLifetime());
			break;
		case SPAWNED:
			Location finalLocation = particle.getItem().getLocation();
			particle.restore();
			plugin.getStorage().getItems().remove(particle.getItem());
			boolean dirty = particle.isStainingMaterial();
			if (dirty)
			{
				bloodStain = new BloodStain(plugin, type, finalLocation);
				int duration = bloodStain.getDuration();
				if (duration > -1)
				{
					plugin.getStorage().getUnbreakables().add(bloodStain.getStainedFloorLocation(), this);
					changeState(State.STAIN, duration);
				}
			}
			break;
		case STAIN:
			bloodStain.restore();
			plugin.getStorage().getUnbreakables().remove(bloodStain.getStainedFloorLocation());
			break;
		default:
			throw new AssertionError();
		}
	}

	public synchronized void restore()
	{

		switch (state)
		{
		case INIT:
			break;
		case SPAWNED:
			particle.restore();
			break;
		case STAIN:
			bloodStain.restore();
			break;
		default:
			throw new AssertionError();
		}
		task.cancel();
		state = State.INVALID;
	}
}
