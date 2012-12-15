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
	private final transient ParticleType type;
	private final transient IBleedingMobs plugin;
	private final transient BukkitScheduler scheduler;
	private transient Location loc;
	private transient BleedCause cause;
	private transient BukkitTask task;
	private transient State state;
	private transient Particle particle;
	private transient BloodStain bloodStain;

	public ParticleStateTask(IBleedingMobs plugin, ParticleType type, Location loc, BleedCause cause)
	{
		this.type = type;
		this.plugin = plugin;
		this.loc = loc;
		this.cause = cause;
		this.scheduler = plugin.getServer().getScheduler();
	}

	public void start()
	{
		changeState(State.INIT, Util.getRandomBetween(0, 3));
	}

	private void changeState(State state, int ticks)
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
		}
		task.cancel();
		state = State.INVALID;
	}
}
