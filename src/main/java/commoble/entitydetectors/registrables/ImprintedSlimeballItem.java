package commoble.entitydetectors.registrables;

import java.util.Optional;

import commoble.entitydetectors.EntityDetectors;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ImprintedSlimeballItem extends Item
{
	public static final String ENTITY_KEY = "entity";

	public ImprintedSlimeballItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public Component getName(ItemStack stack)
	{
		return getEntityType(stack)
			.<Component>map(type -> Component.translatable(this.getDescriptionId())
				.append(Component.literal(" ("))
				.append(Component.translatable(type.getDescriptionId()))
				.append(Component.literal(")")))
			.orElseGet(() -> super.getName(stack));
	}

	public static ItemStack createItemStackForEntityType(EntityType<?> entityType)
	{
		ItemStack stack = new ItemStack(EntityDetectors.IMPRINTED_SLIME_BALL.get());
		ForgeRegistries.ENTITY_TYPES.getHolder(entityType)
			.flatMap(Holder::unwrapKey)
			.ifPresent(key -> stack.getOrCreateTag().putString(ENTITY_KEY, key.location().toString()));
		return stack;
	}

	public static Optional<EntityType<?>> getEntityType(ItemStack stack)
	{
		if (stack.getCount() < 0)
			return Optional.empty();

		CompoundTag nbt = stack.getTag();
		if (nbt == null || !nbt.contains(ENTITY_KEY))
		{
			return Optional.empty();
		}
		// this'll default to pigs if the entity type isn't present but that's probably fine
		ResourceLocation id = new ResourceLocation(nbt.getString(ENTITY_KEY));
		return Optional.ofNullable(ForgeRegistries.ENTITY_TYPES.getValue(id));
	}

	/**
	 * Called to fill the creative tab with the needed item variants
	 */
	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items)
	{
		if (this.allowedIn(tab))
		{
			ForgeRegistries.ENTITY_TYPES.getValues().stream().filter(ImprintedSlimeballItem::isEntityTypeValid)
				.forEachOrdered(entityType -> items.add(createItemStackForEntityType(entityType)));
		}

	}

	public static boolean isEntityTypeValid(EntityType<?> entityType)
	{
		return entityType.getCategory() != MobCategory.MISC;
	}
}
