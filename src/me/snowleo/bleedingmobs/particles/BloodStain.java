package me.snowleo.bleedingmobs.particles;

import me.snowleo.bleedingmobs.IBleedingMobs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;


public class BloodStain
{
	private final transient IBleedingMobs plugin;
	private final transient ParticleType type;
	private transient boolean meltedSnow;
	private transient byte snowData;
	private transient Material savedBlockMat;
	private transient Location savedBlockLoc;
	private transient byte savedBlockData;
	private final transient int duration;

	public BloodStain(IBleedingMobs plugin, ParticleType type, Location loc)
	{
		this.plugin = plugin;
		this.type = type;
		final Block block = getSolidBlock(loc);

		if (canStainBlock(block))
		{
			if (type.isMagicMaterial())
			{
				stainFloor(block, Util.getRandomColor());
			}
			else
			{
				stainFloor(block, type.getWoolColor().getData());
			}
			duration = Util.getRandomBetween(type.getStainLifeFrom(), type.getStainLifeTo());
		} else {
			duration = -1;
		}
	}

	private Block getSolidBlock(Location loc)
	{
		Block block = loc.getBlock();
		if (block == null
			|| block.getType() == Material.AIR
			|| block.getType() == Material.SNOW
			|| block.getType() == Material.WATER
			|| block.getType() == Material.STATIONARY_WATER)
		{
			block = loc.subtract(0, 1, 0).getBlock();
		}
		return block;
	}

	private boolean canStainBlock(Block block)
	{
		return block != null && type.isStainingFloor()
			   && type.getSaturatedMaterials().contains(block.getType())
			   && !plugin.getStorage().getUnbreakables().contains(block.getLocation());
	}

	private void stainFloor(final Block block, final byte color)
	{
		savedBlockMat = block.getType();
		savedBlockLoc = block.getLocation();
		savedBlockData = block.getData();
		block.setTypeIdAndData(Material.WOOL.getId(), color, false);
		final Block snowBlock = block.getRelative(BlockFace.UP);
		if (snowBlock.getType() == Material.SNOW)
		{
			meltedSnow = true;
			snowData = snowBlock.getData();
			snowBlock.setTypeIdAndData(0, (byte)0, false);
		}
		else
		{
			meltedSnow = false;
		}
	}

	public Location getStainedFloorLocation()
	{
		return savedBlockLoc;
	}

	public void restore()
	{
		if (savedBlockLoc != null)
		{
			restoreBlock();
			savedBlockLoc = null;
		}
	}

	private void restoreBlock()
	{
		savedBlockLoc.getBlock().setTypeIdAndData(savedBlockMat.getId(), savedBlockData, false);
		if (meltedSnow)
		{
			savedBlockLoc.getBlock().getRelative(BlockFace.UP).setTypeIdAndData(Material.SNOW.getId(), snowData, false);
		}
		savedBlockLoc = null;
	}

	public int getDuration()
	{
		return duration;
	}
}
