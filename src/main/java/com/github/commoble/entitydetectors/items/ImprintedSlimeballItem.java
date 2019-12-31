package com.github.commoble.entitydetectors.items;

import java.util.EnumSet;
import java.util.Optional;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class ImprintedSlimeballItem extends Item
{
	public static final String ENTITY_KEY = "entity";

	public ImprintedSlimeballItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		ITextComponent baseName = super.getDisplayName(stack);
		return getEntityType(stack).map(entityType -> baseName.appendText(" (").appendSibling(new TranslationTextComponent(entityType.getTranslationKey())).appendText(")"))
			.orElse(baseName);
	}

	public static ItemStack createItemStackForEntityType(EntityType<?> entityType)
	{
		ItemStack stack = new ItemStack(ItemRegistrar.IMPRINTED_SLIME_BALL);
		stack.setTagInfo(ENTITY_KEY, StringNBT.func_229705_a_(entityType.getRegistryName().toString()));
		return stack;
	}

	public static Optional<EntityType<?>> getEntityType(ItemStack stack)
	{
		if (stack.getCount() < 0)
			return Optional.empty();

		CompoundNBT nbt = stack.getTag();
		if (nbt == null || !nbt.contains(ENTITY_KEY))
		{
			return Optional.empty();
		}
		else
		{
			return Optional.of(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(nbt.getString(ENTITY_KEY))));
		}
	}

	public static final EnumSet<EntityClassification> VALID_ENTITY_CLASSIFICATIONS = EnumSet.of(EntityClassification.CREATURE, EntityClassification.MONSTER,
		EntityClassification.WATER_CREATURE);

	/**
	 * Called to fill the creative tab with the needed item variants
	 */
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
	{
		if (this.isInGroup(group))
		{
			ForgeRegistries.ENTITIES.getValues().stream().filter(ImprintedSlimeballItem::isEntityTypeValid)
				.forEachOrdered(entityType -> items.add(createItemStackForEntityType(entityType)));
		}

	}

	public static boolean isEntityTypeValid(EntityType<?> entityType)
	{
		return VALID_ENTITY_CLASSIFICATIONS.contains(entityType.getClassification());
	}
}
