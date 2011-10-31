package me.snowleo.goremod;

import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;


public class ParticleWorldListener extends WorldListener
{
	private final transient IGoreMod goreMod;

	ParticleWorldListener(final IGoreMod goreMod)
	{
		super();
		this.goreMod = goreMod;
	}

	@Override
	public void onChunkUnload(final ChunkUnloadEvent event)
	{
		goreMod.getStorage().removeParticleItemFromChunk(event.getChunk());
		goreMod.getStorage().removeUnbreakableFromChunk(event.getChunk());
	}
}
