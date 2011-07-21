package me.snowleo.goremod;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class GoreMod extends JavaPlugin
{
	private final static int MAX_PARTICLES = 200;
	private final transient Queue<Particle> freeParticles = new LinkedList<Particle>();
	private final transient Set<Particle> particles = new HashSet<Particle>(MAX_PARTICLES);
	private final transient Set<Integer> particleItems = new HashSet<Integer>(MAX_PARTICLES);
	private final transient Set<Location> particleBlocks = new HashSet<Location>(MAX_PARTICLES);

	@Override
	public void onDisable()
	{
		for (Particle particle : particles)
		{
			particle.restore();
		}
		particles.clear();
	}

	@Override
	public void onEnable()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		final EntityListener entityListener = new ParticleEntityListener(this);
		pluginManager.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Low, this);
		pluginManager.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Low, this);
		final PlayerListener playerListener = new ParticlePlayerListener(this);
		pluginManager.registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Low, this);
		final BlockListener blockListener = new ParticleBlockListener(this);

		for (int i = 0; i < MAX_PARTICLES; i++)
		{
			freeParticles.add(new Particle(this));
		}
	}

	public void createParticle(final Location loc)
	{
		final Particle particle = freeParticles.poll();
		if (particle == null)
		{
			return;
		}
		particles.add(particle);
		particle.start(loc);
	}

	public void freeParticle(final Particle particle)
	{
		freeParticles.add(particle);
		particles.remove(particle);
	}

	public void addParticleItem(final int entityId)
	{
		particleItems.add(entityId);
	}

	public void removeParticleItem(final int entityId)
	{
		particleItems.remove(entityId);
	}

	public boolean isParticleItem(final int entityId)
	{
		return particleItems.contains(entityId);
	}

	public void addUnbreakable(final Location blockLocation)
	{
		particleBlocks.add(blockLocation);
	}

	public void removeUnbreakable(final Location blockLocation)
	{
		particleBlocks.remove(blockLocation);
	}

	public boolean isUnbreakable(final Location blockLocation)
	{
		return particleBlocks.contains(blockLocation);
	}
}
