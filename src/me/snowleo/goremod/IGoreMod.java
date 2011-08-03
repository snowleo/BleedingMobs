package me.snowleo.goremod;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;


public interface IGoreMod extends Plugin
{
	void addParticleItem(final int entityId);

	void addUnbreakable(final Location blockLocation);

	void createParticle(final Location loc, final ParticleType type);

	void freeParticle(final Particle particle);

	boolean isParticleItem(final int entityId);

	boolean isUnbreakable(final Location blockLocation);

	void removeParticleItem(final int entityId);

	boolean removeUnbreakable(final Location blockLocation);
}
