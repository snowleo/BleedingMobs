/*
 * BleedingMobs - make your monsters and players bleed
 *
 * Copyright (C) 2011-2012 snowleo
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.snowleo.bleedingmobs.particles;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;


public final class ParticleType
{
	private static final Map<EntityType, ParticleType> MAP = Collections.synchronizedMap(new EnumMap<EntityType, ParticleType>(EntityType.class));

	static
	{
		for (EntityType entityType : EntityType.values())
		{
			if (!entityType.isAlive())
			{
				continue;
			}
			if (entityType == EntityType.CREEPER)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setBoneChance(0).setWoolColor(DyeColor.LIME).setStainsFloor(false).setAmountFrom(5).setAmountTo(15).setParticleMaterial(Material.SULPHUR).build());
			}
			else if (entityType == EntityType.SKELETON || entityType == EntityType.GHAST)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setWoolChance(0).setBoneChance(100).setWoolColor(DyeColor.WHITE).setStainsFloor(false).setAmountFrom(5).setAmountTo(15).setParticleMaterial(Material.BONE).build());
			}
			else if (entityType == EntityType.ENDERMAN)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setWoolColor(DyeColor.BLACK).setParticleMaterial(Material.COAL).build());
			}
			else if (entityType == EntityType.ENDER_DRAGON || entityType == EntityType.WITHER)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setBoneChance(0).setWoolColor(DyeColor.BLACK).setAmountFrom(25).setAmountTo(35).setParticleMaterial(Material.COAL).build());
			}
			else if (entityType == EntityType.CHICKEN)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setWoolChance(20).setBoneChance(0).setAmountFrom(5).setAmountTo(15).setParticleMaterial(Material.FEATHER).build());
			}
			else if (entityType == EntityType.BAT)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setWoolChance(20).setBoneChance(0).setAmountFrom(5).setAmountTo(15).setParticleMaterial(Material.COAL).build());
			}
			else if (entityType == EntityType.SLIME || entityType == EntityType.MAGMA_CUBE)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setWoolChance(0).setBoneChance(0).setWoolColor(DyeColor.GREEN).setAmountFrom(5).setAmountTo(15).setParticleMaterial(Material.SLIME_BALL).build());
			}
			else if (entityType == EntityType.BLAZE)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setWoolChance(0).setBoneChance(0).setWoolColor(DyeColor.YELLOW).setParticleMaterial(Material.BLAZE_POWDER).build());
			}
			else if (entityType == EntityType.IRON_GOLEM)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setBoneChance(0).setWoolColor(DyeColor.SILVER).setParticleMaterial(Material.IRON_INGOT).build());
			}
			else if (entityType == EntityType.SNOWMAN)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setBoneChance(0).setWoolColor(DyeColor.WHITE).setParticleMaterial(Material.SNOW_BALL).build());
			}
			else if (entityType == EntityType.MUSHROOM_COW)
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).setParticleMaterial(Material.RED_MUSHROOM).build());
			}
			else
			{
				MAP.put(entityType, new ParticleType.Builder(entityType).build());
			}
		}
	}

	public static Set<EntityType> keys()
	{
		synchronized (MAP)
		{
			return EnumSet.copyOf(MAP.keySet());
		}
	}

	public static ParticleType get(final EntityType entityType)
	{
		return MAP.get(entityType);
	}

	public static void save(final ParticleType type)
	{
		MAP.put(type.getEntityType(), type);
	}

	public static Builder getBuilder(final EntityType type)
	{
		ParticleType p = get(type);
		if (p == null)
		{
			throw new IllegalStateException();
		}
		ParticleType.Builder builder = new ParticleType.Builder(p.getEntityType());
		builder.setAmountFrom(p.getAmountFrom());
		builder.setAmountTo(p.getAmountTo());
		builder.setBoneChance(p.getBoneChance());
		builder.setBoneLife(p.getBoneLife());
		builder.setParticleLifeFrom(p.getParticleLifeFrom());
		builder.setParticleLifeTo(p.getParticleLifeTo());
		builder.setParticleMaterial(p.getParticleMaterial());
		builder.setSaturatedMats(p.getSaturatedMaterials());
		builder.setStainLifeFrom(p.getStainLifeFrom());
		builder.setStainLifeTo(p.getStainLifeTo());
		builder.setStainsFloor(p.isStainingFloor());
		builder.setWoolChance(p.getWoolChance());
		builder.setWoolColor(p.getWoolColor());
		return builder;
	}
	private final EntityType entityType;
	private final String entityName;
	private final int woolChance;
	private final int boneChance;
	private final int particleLifeFrom;
	private final int particleLifeTo;
	private final DyeColor woolColor;
	private final boolean stainsFloor;
	private final int boneLife;
	private final int stainLifeFrom;
	private final int stainLifeTo;
	private final int amountFrom;
	private final int amountTo;
	private final MaterialData particleMaterial;
	private final EnumSet<Material> saturatedMats;

	private ParticleType(final Builder builder)
	{
		this.entityType = builder.getEntityType();
		this.entityName = builder.getEntityName();
		this.woolChance = builder.getWoolChance();
		this.boneChance = builder.getBoneChance();
		this.particleLifeFrom = builder.getParticleLifeFrom();
		this.particleLifeTo = builder.getParticleLifeTo();
		this.woolColor = builder.getWoolColor();
		this.stainsFloor = builder.isStainsFloor();
		this.boneLife = builder.getBoneLife();
		this.stainLifeFrom = builder.getStainLifeFrom();
		this.stainLifeTo = builder.getStainLifeTo();
		this.amountFrom = builder.getAmountFrom();
		this.amountTo = builder.getAmountTo();
		this.particleMaterial = builder.getParticleMaterial();
		this.saturatedMats = builder.getSaturatedMats();
	}

	public int getWoolChance()
	{
		return woolChance;
	}

	public int getBoneChance()
	{
		return boneChance;
	}

	public int getParticleLifeFrom()
	{
		return particleLifeFrom;
	}

	public int getParticleLifeTo()
	{
		return particleLifeTo;
	}

	public DyeColor getWoolColor()
	{
		return woolColor;
	}

	public boolean isStainingFloor()
	{
		return stainsFloor;
	}

	public int getBoneLife()
	{
		return boneLife;
	}

	public int getStainLifeFrom()
	{
		return stainLifeFrom;
	}

	public int getStainLifeTo()
	{
		return stainLifeTo;
	}

	public int getAmountFrom()
	{
		return amountFrom;
	}

	public int getAmountTo()
	{
		return amountTo;
	}

	public EnumSet<Material> getSaturatedMaterials()
	{
		return saturatedMats;
	}

	public MaterialData getParticleMaterial()
	{
		return particleMaterial;
	}

	public EntityType getEntityType()
	{
		return entityType;
	}

	@Override
	public String toString()
	{
		return entityName;
	}

	public boolean isMagicMaterial()
	{
		return this.getParticleMaterial().getItemType() == Material.CAKE;
	}


	public static class Builder
	{
		private final EntityType entityType;
		private final String entityName;
		private int woolChance = 50;
		private int boneChance = 50;
		private int particleLifeFrom = 5;
		private int particleLifeTo = 15;
		private DyeColor woolColor = DyeColor.RED;
		private boolean stainsFloor = true;
		private int boneLife = 100;
		private int stainLifeFrom = 80;
		private int stainLifeTo = 120;
		private int amountFrom = 15;
		private int amountTo = 25;
		private MaterialData particleMaterial = new MaterialData(Material.REDSTONE);
		private EnumSet<Material> saturatedMats = EnumSet.copyOf(Arrays.asList(new Material[]
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
					Material.SNOW_BLOCK,
					Material.BRICK,
					Material.MOSSY_COBBLESTONE
				}));

		public Builder(final EntityType entityType)
		{
			this.entityType = entityType;
			this.entityName = entityType.toString().replaceAll("_", "");
		}

		public EntityType getEntityType()
		{
			return entityType;
		}

		public String getEntityName()
		{
			return entityName;
		}

		public int getWoolChance()
		{
			return woolChance;
		}

		public Builder setWoolChance(final int woolChance)
		{
			this.woolChance = woolChance;
			return this;
		}

		public int getBoneChance()
		{
			return boneChance;
		}

		public Builder setBoneChance(final int boneChance)
		{
			this.boneChance = boneChance;
			return this;
		}

		public int getParticleLifeFrom()
		{
			return particleLifeFrom;
		}

		public Builder setParticleLifeFrom(final int particleLifeFrom)
		{
			this.particleLifeFrom = particleLifeFrom;
			return this;
		}

		public int getParticleLifeTo()
		{
			return particleLifeTo;
		}

		public Builder setParticleLifeTo(final int particleLifeTo)
		{
			this.particleLifeTo = particleLifeTo;
			return this;
		}

		public DyeColor getWoolColor()
		{
			return woolColor;
		}

		public Builder setWoolColor(final DyeColor woolColor)
		{
			this.woolColor = woolColor;
			return this;
		}

		public boolean isStainsFloor()
		{
			return stainsFloor;
		}

		public Builder setStainsFloor(final boolean stainsFloor)
		{
			this.stainsFloor = stainsFloor;
			return this;
		}

		public int getBoneLife()
		{
			return boneLife;
		}

		public Builder setBoneLife(final int boneLife)
		{
			this.boneLife = boneLife;
			return this;
		}

		public int getStainLifeFrom()
		{
			return stainLifeFrom;
		}

		public Builder setStainLifeFrom(final int stainLifeFrom)
		{
			this.stainLifeFrom = stainLifeFrom;
			return this;
		}

		public int getStainLifeTo()
		{
			return stainLifeTo;
		}

		public Builder setStainLifeTo(final int stainLifeTo)
		{
			this.stainLifeTo = stainLifeTo;
			return this;
		}

		public int getAmountFrom()
		{
			return amountFrom;
		}

		public Builder setAmountFrom(final int amountFrom)
		{
			this.amountFrom = amountFrom;
			return this;
		}

		public int getAmountTo()
		{
			return amountTo;
		}

		public Builder setAmountTo(final int amountTo)
		{
			this.amountTo = amountTo;
			return this;
		}

		public MaterialData getParticleMaterial()
		{
			return particleMaterial;
		}

		public Builder setParticleMaterial(final Material particleMaterial)
		{
			this.particleMaterial = new MaterialData(particleMaterial);
			return this;
		}

		public Builder setParticleMaterial(final MaterialData particleMaterial)
		{
			this.particleMaterial = particleMaterial;
			return this;
		}

		public EnumSet<Material> getSaturatedMats()
		{
			return saturatedMats;
		}

		public Builder setSaturatedMats(final EnumSet<Material> saturatedMats)
		{
			this.saturatedMats = saturatedMats;
			return this;
		}

		@Override
		public String toString()
		{
			return entityName;
		}

		public ParticleType build()
		{
			return new ParticleType(this);
		}
	}
}
