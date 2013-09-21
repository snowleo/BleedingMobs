package me.snowleo.bleedingmobs.particles;

import me.snowleo.bleedingmobs.IBleedingMobs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;


public class BloodStain
{
	private static final class StainedBlock
	{
		private final Location location;
		private final Material material;
		private final byte data;
		private final boolean meltedSnow;
		private final byte snowData;

		private StainedBlock(final Block block, final byte color)
		{
			location = block.getLocation();
			material = block.getType();
			data = block.getData();
			block.setTypeIdAndData(Material.WOOL.getId(), color, false);
			Block snowBlock = block.getRelative(BlockFace.UP);
			if (snowBlock.getType() == Material.SNOW)
			{
				meltedSnow = true;
				snowData = snowBlock.getData();
				snowBlock.setTypeIdAndData(0, (byte)0, false);
			}
			else
			{
				meltedSnow = false;
				snowData = 0;
			}
		}

		private Location getLocation()
		{
			return location;
		}

		private void restoreBlock()
		{
			Block block = location.getBlock();
			block.setTypeIdAndData(material.getId(), data, false);
			if (meltedSnow)
			{
				block.getRelative(BlockFace.UP).setTypeIdAndData(Material.SNOW.getId(), snowData, false);
			}
		}
	}
	private final IBleedingMobs plugin;
	private final ParticleType type;
	private final int duration;
	private final StainedBlock stainedBlock;

	public BloodStain(final IBleedingMobs plugin, final ParticleType type, final Location loc)
	{
		this.plugin = plugin;
		this.type = type;
		Block block = getSolidBlock(loc);

		if (canStainBlock(block))
		{
			if (type.isMagicMaterial())
			{
				stainedBlock = new StainedBlock(block, Util.getRandomColor());
			}
			else
			{
				stainedBlock = new StainedBlock(block, type.getWoolColor().getWoolData());
			}
			duration = Util.getRandomBetween(type.getStainLifeFrom(), type.getStainLifeTo());
		}
		else
		{
			stainedBlock = null;
			duration = -1;
		}
	}

	private Block getSolidBlock(final Location loc)
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

	private boolean canStainBlock(final Block block)
	{
		return block != null && type.isStainingFloor()
			   && type.getSaturatedMaterials().contains(block.getType())
			   && !plugin.getStorage().getUnbreakables().contains(block.getLocation());
	}

	public Location getStainedFloorLocation()
	{
		return stainedBlock == null ? null : stainedBlock.getLocation();
	}

	public void restore()
	{
		if (stainedBlock != null) {
			stainedBlock.restoreBlock();
		}
	}

	public int getDuration()
	{
		return duration;
	}
}
