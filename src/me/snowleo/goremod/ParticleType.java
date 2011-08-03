package me.snowleo.goremod;


public enum ParticleType
{
	DEATH(40, 12, 5, 15, 14, true, 100, 80, 120, 25, 35),
	ATTACK(50, 6, 5, 15, 14, true, 100, 80, 120, 15, 25),
	PROJECTILE(50, 6, 5, 15, 14, true, 100, 80, 120, 5, 15),
	CREEPER(100, 0, 5, 15, 5, false, 100, 80, 120, 10, 20);
	private int woolChance;
	private int boneChance;
	private int particleLifeFrom;
	private int particleLifeTo;
	private int woolColor;
	private boolean stainsFloor;
	private int boneLife;
	private int stainLifeFrom;
	private int stainLifeTo;
	private int amountFrom;
	private int amountTo;

	private ParticleType(final int woolChance, final int boneChance, final int particleLifeFrom,
						 final int particleLifeTo, final int woolColor, final boolean stainsFloor,
						 final int boneLife, final int stainLifeFrom, final int stainLifeTo,
						 final int amountFrom, final int amountTo)
	{
		this.woolChance = woolChance;
		this.boneChance = boneChance;
		this.particleLifeFrom = particleLifeFrom;
		this.particleLifeTo = particleLifeTo;
		this.woolColor = woolColor;
		this.stainsFloor = stainsFloor;
		this.boneLife = boneLife;
		this.stainLifeFrom = stainLifeFrom;
		this.stainLifeTo = stainLifeTo;
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
}
