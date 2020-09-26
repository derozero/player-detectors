package commoble.entitydetectors;
import java.util.function.Consumer;

import commoble.entitydetectors.blocks.BlockRegistrar;
import commoble.entitydetectors.blocks.TileEntityRegistrar;
import commoble.entitydetectors.client.ClientEvents;
import commoble.entitydetectors.items.ImprintedSlimeballItem;
import commoble.entitydetectors.items.ItemRegistrar;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod(EntityDetectors.MODID)
public class EntityDetectors
{
	public static final String MODID = "entitydetectors";
	
	public static Config config;
	
	public EntityDetectors()
	{		
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		
		config = ConfigHelper.register(ModConfig.Type.SERVER, Config::new);
		
		modBus.addGenericListener(Block.class, getRegistryHandler(BlockRegistrar::onRegisterBlocks));
		modBus.addGenericListener(Item.class, getRegistryHandler(ItemRegistrar::onRegisterItems));
		modBus.addGenericListener(TileEntityType.class, getRegistryHandler(TileEntityRegistrar::onRegisterTileEntities));
		
		forgeBus.addListener(EntityDetectors::onPlayerInteractWithEntity);
		forgeBus.addListener(EntityDetectors::onPlayerAttackEntity);
		
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientEvents.subscribeClientEvents(modBus, forgeBus));
	}
	
	public static void onPlayerInteractWithEntity(PlayerInteractEvent.EntityInteract event)
	{
		PlayerEntity player = event.getPlayer();
		ItemStack oldStack = event.getItemStack();
		EntityType<?> entityType = event.getTarget().getType();
		onPlayerAttackedOrInteractedWithEntity(player, oldStack, entityType);
	}
	
	public static void onPlayerAttackEntity(AttackEntityEvent event)
	{
		PlayerEntity player = event.getPlayer();
		ItemStack oldStack = player.getHeldItemMainhand();
		EntityType<?> entityType = event.getTarget().getType();
		onPlayerAttackedOrInteractedWithEntity(player, oldStack, entityType);
	}
	
	public static void onPlayerAttackedOrInteractedWithEntity(PlayerEntity player, ItemStack oldStack, EntityType<?> entityType)
	{
		if (oldStack.getItem() == Items.SLIME_BALL && ImprintedSlimeballItem.isEntityTypeValid(entityType))
		{
			oldStack.shrink(1);
			ItemStack newSlimeStack = ImprintedSlimeballItem.createItemStackForEntityType(entityType);
			if (!player.addItemStackToInventory(newSlimeStack))
			{
				player.dropItem(newSlimeStack, false);
			}
		}
	}
	
	public static <T extends IForgeRegistryEntry<T>> Consumer<Register<T>> getRegistryHandler(Consumer<Registrator<T>> consumer)
	{
		return event -> consumer.accept(new Registrator<T>(event.getRegistry()));
	}
}
