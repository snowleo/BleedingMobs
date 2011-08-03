package me.snowleo.goremod;

import java.util.Arrays;
import java.util.EnumSet;
import org.bukkit.Material;


public enum ParticleType
{
	DEATH(40, 12, 5, 15, 14, true, 25, 35),
	ATTACK(50, 6, 5, 15, 14, true, 15, 25),
	PROJECTILE(50, 6, 5, 15, 14, true, 5, 15),
	CREEPER(100, 0, 5, 15, 5, false, 5, 15);
	private int woolChance;
	private int boneChance;
	private int particleLifeFrom;
	private int particleLifeTo;
	private int woolColor;
	private boolean stainsFloor;
	private int boneLife = 100;
	private int stainLifeFrom = 80;
	private int stainLifeTo = 120;
	private int amountFrom;
	private int amountTo;
	private transient EnumSet<Material> saturatedMats = EnumSet.copyOf(Arrays.asList(new Material[]
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
			}));

	private ParticleType(final int woolChance, final int boneChance, final int particleLifeFrom,
						 final int particleLifeTo, final int woolColor, final boolean stainsFloor,
						 final int amountFrom, final int amountTo)
	{
		this.woolChance = woolChance;
		this.boneChance = boneChance;
		this.particleLifeFrom = particleLifeFrom;
		this.particleLifeTo = particleLifeTo;
		this.woolColor = woolColor;
		this.stainsFloor = stainsFloor;
		this.amountFrom = amountFrom;
		this.amountTo = amountTo;
	}

	public int getWoolChance()
	{
		return woolChance;
	}

	public void setWoolChance(final int woolChance)
	{
		this.woolChance = woolChance;
	}

	public int getBoneChance()
	{
		return boneChance;
	}

	public void setBoneChance(final int boneChance)
	{
		this.boneChance = boneChance;
	}

	public int getParticleLifeFrom()
	{
		return particleLifeFrom;
	}

	public void setParticleLifeFrom(final int particleLifeFrom)
	{
		this.particleLifeFrom = particleLifeFrom;
	}

	public int getParticleLifeTo()
	{
		return particleLifeTo;
	}

	public void setParticleLifeTo(final int particleLifeTo)
	{
		this.particleLifeTo = particleLifeTo;
	}

	public int getWoolColor()
	{
		return woolColor;
	}

	public void setWoolColor(final int woolColor)
	{
		this.woolColor = woolColor;
	}

	public boolean isStainingFloor()
	{
		return stainsFloor;
	}

	public void setStainsFloor(final boolean stainsFloor)
	{
		this.stainsFloor = stainsFloor;
	}

	public int getBoneLife()
	{
		return boneLife;
	}

	public void setBoneLife(final int boneLife)
	{
		this.boneLife = boneLife;
	}

	public int getStainLifeFrom()
	{
		return stainLifeFrom;
	}

	public void setStainLifeFrom(final int stainLifeFrom)
	{
		this.stainLifeFrom = stainLifeFrom;
	}

	public int getStainLifeTo()
	{
		return stainLifeTo;
	}

	public void setStainLifeTo(final int stainLifeTo)
	{
		this.stainLifeTo = stainLifeTo;
	}

	public int getAmountFrom()
	{
		return amountFrom;
	}

	public void setAmountFrom(final int amountFrom)
	{
		this.amountFrom = amountFrom;
	}

	public int getAmountTo()
	{
		return amountTo;
	}

	public void setAmountTo(final int amountTo)
	{
		this.amountTo = amountTo;
	}

	public EnumSet<Material> getSaturatedMaterials()
	{
		return saturatedMats;
	}

	public void setSaturatedMaterials(EnumSet<Material> saturatedMats)
	{
		this.saturatedMats = saturatedMats;
	}
}
