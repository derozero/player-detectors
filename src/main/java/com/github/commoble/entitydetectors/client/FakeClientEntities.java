package com.github.commoble.entitydetectors.client;

import java.util.Optional;

import com.github.commoble.entitydetectors.util.ClassHelper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class FakeClientEntities
{
	private static final LoadingCache<EntityContext<?>, Entity> ENTITY_LOADER = CacheBuilder.newBuilder()
		.build(
			new CacheLoader<EntityContext<?>, Entity>()
			{
				@Override
				public Entity load(EntityContext<?> context)
				{
					return context.createEntity();
				}
			}
		);
	
	public static <T extends Entity> Entity getFakeEntity(EntityType<T> type, World world)
	{
		if (type == null)
			return null;
		return ENTITY_LOADER.getUnchecked(EntityContext.of(type, world));
	}
	
	public static Optional<Entity> getOptionalFakeEntity(Optional<EntityType<?>> maybeType, World world)
	{
		return maybeType.map(type -> ENTITY_LOADER.getUnchecked(EntityContext.of(type, world)));
	}
	
	public static class EntityContext<T extends Entity>
	{
		public final EntityType<T> type;
		public final World world;
		
		private EntityContext(EntityType<T> type, World world)
		{
			this.type = type;
			this.world = world;
		}
		
		public static <T extends Entity> EntityContext<T> of(EntityType<T> type, World world)
		{
			return new EntityContext<T>(type, world);
		}
		
		public T createEntity()
		{
			return this.type.create(this.world);
		}
		
		@Override
		public int hashCode()
		{
			return this.type.getRegistryName().hashCode() + this.world.hashCode();
		}
		
		@Override
		public boolean equals(Object obj)
		{
			return ClassHelper.as(obj, EntityContext.class)
				.map(other -> this.type.getRegistryName().equals(other.type.getRegistryName())
					&& this.world == other.world)
				.orElse(false);
		}
	}
}
