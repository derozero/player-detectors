package commoble.entitydetectors.items;

import commoble.entitydetectors.EntityDetectors;
import commoble.entitydetectors.Registrator;
import commoble.entitydetectors.RegistryNames;
import commoble.entitydetectors.ResourceLocations;
import commoble.entitydetectors.blocks.BlockRegistrar;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(EntityDetectors.MODID)
public class ItemRegistrar
{
	@ObjectHolder(RegistryNames.PLAYER_DETECTOR)
	public static final BlockItem PLAYER_DETECTOR = null;
	
	@ObjectHolder(RegistryNames.MOB_DETECTOR)
	public static final BlockItem MOB_DETECTOR = null;
	
	@ObjectHolder(RegistryNames.IMPRINTED_SLIME_BALL)
	public static final ImprintedSlimeballItem IMPRINTED_SLIME_BALL = null;
	
	public static void onRegisterItems(Registrator<Item> reg)
	{
		reg.register(ResourceLocations.PLAYER_DETECTOR, new BlockItem(BlockRegistrar.PLAYER_DETECTOR, new Item.Properties().group(ItemGroup.REDSTONE)));
		reg.register(ResourceLocations.MOB_DETECTOR, new BlockItem(BlockRegistrar.MOB_DETECTOR, new Item.Properties().group(ItemGroup.REDSTONE)));
		reg.register(ResourceLocations.IMPRINTED_SLIME, new ImprintedSlimeballItem(new Item.Properties().group(ItemGroup.MISC)));
	}
}
