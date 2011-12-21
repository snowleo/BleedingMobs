package me.Perdog.BleedingMobs;

import java.util.UUID;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * This class is for compatibility with NoLagg, since it uses this class.
 * It's possible that this is removed in the future.
 * @deprecated Use the interface IBleedingMobs instead.
 */

@Deprecated
public abstract class BleedingMobs extends JavaPlugin
{
	public abstract boolean isSpawning();
	public abstract boolean isWorldEnabled(World world);
	public abstract boolean isParticleItem(UUID id);
}
