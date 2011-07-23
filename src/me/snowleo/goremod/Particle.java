package me.snowleo.goremod;

import java.util.Arrays;
import java.util.Random;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;


public class Particle implements Runnable
{
	private transient Material mat;
	private transient Item item;
	private final transient BukkitScheduler scheduler;
	private final transient GoreMod goreMod;
	private final transient Random random = new Random();
	private transient boolean meltedSnow;


	enum State
	{
		UNKNOWN,
		SPAWNED,
		FLOWING
	}
	private transient State state = State.UNKNOWN;
	private final transient List<Material> saturatedMats = Arrays.asList(new Material[]
			{
				Material.GRASS,
				Material.DIRT,
				Material.STONE,
				Material.COBBLESTONE,
				Material.SAND,
				Material.SANDSTONE,
				Material.WOOD,
				Material.GRAVEL,
				Material.WOOL,
				Material.DOUBLE_STEP,
				Material.SOUL_SAND,
				Material.NETHERRACK,
				Material.CLAY,
				Material.SNOW_BLOCK
			});
	private transient Material savedBlockMaterial;
	private transient Location savedBlockLocation;
	private transient byte savedBlockData;

	public Particle(final GoreMod goreMod)
	{
		this.goreMod = goreMod;
		this.scheduler = goreMod.getServer().getScheduler();
	}

	public void start(final Location loc)
	{
		ItemStack stack;
		if (Math.abs(random.nextInt()) % 20 < 10)
		{
			mat = Material.WOOL;
			stack = new ItemStack(mat, 1, (short)14);
		}
		else if (Math.abs(random.nextInt()) % 20 < 3)
		{
			mat = Material.BONE;
			stack = new ItemStack(mat, 1);
		}
		else
		{
			mat = Material.REDSTONE;
			stack = new ItemStack(mat, 1);
		}
		item = loc.getWorld().dropItemNaturally(loc, stack);
		goreMod.addParticleItem(((CraftItem)item).getEntityId());
		state = State.SPAWNED;
		scheduler.scheduleSyncDelayedTask(goreMod, this, Math.abs(random.nextInt()) % 10 + 5);
	}

	@Override
	public void run()
	{
		if (state == State.SPAWNED)
		{
			if (mat == Material.REDSTONE || mat == Material.WOOL)
			{
				Block block = item.getLocation().getBlock();
				if (block == null || block.getType() == Material.AIR || block.getType() == Material.SNOW)
				{
					block = item.getLocation().getBlock().getRelative(BlockFace.DOWN);
				}
				if (block != null && saturatedMats.contains(block.getType()) && !(block.getType() == Material.WOOL && block.getData() == 14))
				{
					savedBlockMaterial = block.getType();
					savedBlockLocation = block.getLocation();
					savedBlockData = block.getData();
					goreMod.addUnbreakable(savedBlockLocation);
					block.setTypeIdAndData(Material.WOOL.getId(), (byte)14, true);
					if (block.getRelative(BlockFace.UP).getType() == Material.SNOW)
					{
						meltedSnow = true;
						block.getRelative(BlockFace.UP).setType(Material.AIR);
					}
					else
					{
						meltedSnow = false;
					}
					state = State.FLOWING;
					scheduler.scheduleSyncDelayedTask(goreMod, this, Math.abs(random.nextInt()) % 40 + 80);
				}
				else
				{
					goreMod.freeParticle(this);
				}
				item.remove();
				goreMod.removeParticleItem(((CraftItem)item).getEntityId());
				return;
			}
			if (mat == Material.BONE)
			{
				((CraftItem)item).getHandle().damageEntity(null, 1);
				if (item.isDead())
				{
					item.remove();
					goreMod.removeParticleItem(((CraftItem)item).getEntityId());
					goreMod.freeParticle(this);
				}
				else
				{
					scheduler.scheduleSyncDelayedTask(goreMod, this, 20);
				}
				return;
			}
		}
		if (state == State.FLOWING)
		{
			restoreBlock(true);
		}
	}

	void restore()
	{
		if (state == State.SPAWNED)
		{
			state = State.UNKNOWN;
			item.remove();
		}
		if (state == State.FLOWING)
		{
			state = State.UNKNOWN;
			restoreBlock(false);
		}
	}

	private void restoreBlock(final boolean removeFromSet)
	{
		final boolean notExploded = goreMod.removeUnbreakable(savedBlockLocation);
		if (notExploded)
		{
			savedBlockLocation.getBlock().setTypeIdAndData(savedBlockMaterial.getId(), savedBlockData, false);
			if (meltedSnow)
			{
				savedBlockLocation.getBlock().getRelative(BlockFace.UP).setType(Material.SNOW);
			}
		}
		else
		{
			savedBlockLocation.getBlock().setType(Material.AIR);
		}
		if (removeFromSet)
		{
			goreMod.freeParticle(this);
		}
	}
}
