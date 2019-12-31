package com.github.commoble.entitydetectors.client;

import java.util.Optional;

import com.github.commoble.entitydetectors.items.ImprintedSlimeballItem;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;

public class ImprintedSlimeballItemColor implements IItemColor
{
	public static final int NO_TINT = 0xFFFFFFFF;
	@Override
	public int getColor(ItemStack stack, int tintIndex)
	{
		return ImprintedSlimeballItem.getEntityType(stack)
			.flatMap(ImprintedSlimeballItemColor::getSpawnEggForEntityType)
			.map(egg -> egg.getColor(tintIndex))
			.orElse(NO_TINT);
			
	}

	private static Optional<SpawnEggItem> getSpawnEggForEntityType(EntityType<?> type)
	{
		return Optional.ofNullable(SpawnEggItem.getEgg(type));
	}
}
