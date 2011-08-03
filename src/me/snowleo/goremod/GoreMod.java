package me.snowleo.goremod;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.bukkit.util.config.Configuration;


public class GoreMod extends JavaPlugin implements IGoreMod
{
	private final static int MAX_PARTICLES = 200;
	private final transient Queue<Particle> freeParticles = new LinkedList<Particle>();
	private transient Set<Particle> particles;
	private transient Set<Integer> particleItems;
	private transient Set<Vector> particleBlocks;
	private transient Set<String> worlds = Collections.emptySet();
	private final transient Random random = new Random();

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
		final int maxParticles = loadConfig();
		for (int i = 0; i < maxParticles; i++)
		{
			freeParticles.add(new Particle(this));
		}

		registerListeners();

		final String loadMessage = getDescription().getFullName() + " loaded. Have fun!";
		getServer().getLogger().info(loadMessage);
	}

	private void registerListeners()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		final EntityListener entityListener = new ParticleEntityListener(this);
		pluginManager.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Low, this);
		pluginManager.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Low, this);
		pluginManager.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
		final PlayerListener playerListener = new ParticlePlayerListener(this);
		pluginManager.registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Low, this);
		final BlockListener blockListener = new ParticleBlockListener(this);
		pluginManager.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Low, this);
	}

	private int loadConfig()
	{
		final Configuration config = this.getConfiguration();
		config.load();
		final int maxParticles = Math.max(20, config.getInt("max-particles", MAX_PARTICLES));
		particles = new HashSet<Particle>(maxParticles);
		particleItems = new HashSet<Integer>(maxParticles);
		particleBlocks = new HashSet<Vector>(maxParticles);
		for (ParticleType particleType : ParticleType.values())
		{
			final String name = particleType.toString().toLowerCase();
			particleType.setWoolChance(Math.min(100, Math.max(0, config.getInt(name + ".wool-chance", particleType.getWoolChance()))));
			particleType.setBoneChance(Math.min(100, Math.max(0, config.getInt(name + ".bone-chance", particleType.getBoneChance()))));
			particleType.setParticleLifeFrom(Math.max(0, config.getInt(name + ".particle-life.from", particleType.getParticleLifeFrom())));
			particleType.setParticleLifeTo(Math.max(particleType.getParticleLifeFrom(), config.getInt(name + ".particle-life.to", particleType.getParticleLifeTo())));
			particleType.setWoolColor(Math.min(15, Math.max(0, config.getInt(name + ".wool-color", particleType.getWoolColor()))));
			particleType.setStainsFloor(config.getBoolean(name + ".stains-floor", particleType.isStainingFloor()));
			particleType.setBoneLife(Math.max(0, config.getInt(name + ".bone-life", particleType.getBoneLife())));
			particleType.setStainLifeFrom(Math.max(0, config.getInt(name + ".stain-life.from", particleType.getStainLifeFrom())));
			particleType.setStainLifeTo(Math.max(particleType.getStainLifeFrom(), config.getInt(name + ".stain-life.to", particleType.getStainLifeTo())));
			particleType.setAmountFrom(Math.max(0, config.getInt(name + ".amount.from", particleType.getAmountFrom())));
			particleType.setAmountTo(Math.max(particleType.getAmountFrom(), config.getInt(name + ".amount.to", particleType.getAmountTo())));
		}
		final Collection coll = config.getStringList("worlds", null);
		worlds = new HashSet(coll == null ? Collections.emptyList() : coll);
		config.setProperty("worlds", worlds.toArray(new String[0]));
		config.save();
		return maxParticles;
	}

	@Override
	public void createParticle(final Location loc, final ParticleType type)
	{
		if (!worlds.isEmpty() && !worlds.contains(loc.getWorld().getName()))
		{
			return;
		}
		final int amount = random.nextInt(type.getAmountTo() - type.getAmountFrom()) + type.getAmountFrom();
		for (int i = 0; i < amount; i++)
		{
			final Particle particle = freeParticles.poll();
			if (particle == null)
			{
				return;
			}
			particles.add(particle);
			particle.start(loc, type);
		}
	}

	@Override
	public void freeParticle(final Particle particle)
	{
		freeParticles.add(particle);
		particles.remove(particle);
	}

	@Override
	public void addParticleItem(final int entityId)
	{
		particleItems.add(entityId);
	}

	@Override
	public void removeParticleItem(final int entityId)
	{
		particleItems.remove(entityId);
	}

	@Override
	public boolean isParticleItem(final int entityId)
	{
		return particleItems.contains(entityId);
	}

	@Override
	public void addUnbreakable(final Location blockLocation)
	{
		particleBlocks.add(blockLocation.toVector());
	}

	@Override
	public boolean removeUnbreakable(final Location blockLocation)
	{
		return particleBlocks.remove(blockLocation.toVector());
	}

	@Override
	public boolean isUnbreakable(final Location blockLocation)
	{
		return particleBlocks.contains(blockLocation.toVector());
	}
}
