package commoble.entitydetectors;
import commoble.entitydetectors.client.ClientProxy;
import commoble.entitydetectors.registrables.EntityDetectorBlock;
import commoble.entitydetectors.registrables.ImprintedSlimeballItem;
import commoble.entitydetectors.registrables.MobDetectorBlock;
import commoble.entitydetectors.registrables.MobDetectorBlockEntity;
import commoble.entitydetectors.registrables.PlayerDetectorBlock;
import commoble.entitydetectors.registrables.PlayerDetectorBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

@Mod(EntityDetectors.MODID)
public class EntityDetectors
{
	public static final String MODID = "entitydetectors";
	
	public static final class Tags
	{
		private Tags() {}
		public static final class Items
		{
			private Items() {}
			public static final TagKey<Item> MOB_DETECTOR_FILTERS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MODID, "mob_detector_filters"));
			public static final TagKey<Item> IMPRINTABLE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MODID, "imprintable"));
		}
	}
	
	public static final CreativeModeTab TAB = new CreativeModeTab(MODID)
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(MOB_DETECTOR.get());
		}
	};
	
	public static final CommonConfig COMMON_CONFIG = ConfigHelper.register(ModConfig.Type.COMMON, CommonConfig::create);
	
	private static final DeferredRegister<Block> BLOCKS = defreg(ForgeRegistries.BLOCKS);
	private static final DeferredRegister<Item> ITEMS = defreg(ForgeRegistries.ITEMS);
	private static final DeferredRegister<BlockEntityType<?>> BETS = defreg(ForgeRegistries.BLOCK_ENTITY_TYPES);
	
	public static final RegistryObject<EntityDetectorBlock> PLAYER_DETECTOR = BLOCKS.register(Names.PLAYER_DETECTOR, () -> new PlayerDetectorBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F).lightLevel(EntityDetectorBlock::getLightValue).noOcclusion()));
	public static final RegistryObject<MobDetectorBlock> MOB_DETECTOR = BLOCKS.register(Names.MOB_DETECTOR, () -> new MobDetectorBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F).lightLevel(EntityDetectorBlock::getLightValue).noOcclusion()));
	
	public static final RegistryObject<BlockItem> PLAYER_DETECTOR_ITEM = ITEMS.register(Names.PLAYER_DETECTOR, () -> new BlockItem(PLAYER_DETECTOR.get(), new Item.Properties().tab(TAB)));
	public static final RegistryObject<BlockItem> MOB_DETECTOR_ITEM = ITEMS.register(Names.MOB_DETECTOR, () -> new BlockItem(MOB_DETECTOR.get(), new Item.Properties().tab(TAB)));
	public static final RegistryObject<ImprintedSlimeballItem> IMPRINTED_SLIME_BALL = ITEMS.register(Names.IMPRINTED_SLIME_BALL, () -> new ImprintedSlimeballItem(new Item.Properties().tab(TAB)));

	public static final RegistryObject<BlockEntityType<PlayerDetectorBlockEntity>> PLAYER_DETECTOR_BET = BETS.register(Names.PLAYER_DETECTOR, () -> BlockEntityType.Builder.of(PlayerDetectorBlockEntity::create, PLAYER_DETECTOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<MobDetectorBlockEntity>> MOB_DETECTOR_BET = BETS.register(Names.MOB_DETECTOR, () -> BlockEntityType.Builder.of(MobDetectorBlockEntity::create, MOB_DETECTOR.get()).build(null));
	
	public EntityDetectors()
	{		
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		
		forgeBus.addListener(EntityDetectors::onPlayerInteractWithEntity);
		forgeBus.addListener(EntityDetectors::onPlayerAttackEntity);
		
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			ClientProxy.subscribeClientEvents(modBus, forgeBus);
		}
	}
	
	public static void onPlayerInteractWithEntity(PlayerInteractEvent.EntityInteract event)
	{
		Player player = event.getEntity();
		ItemStack oldStack = event.getItemStack();
		EntityType<?> entityType = event.getTarget().getType();
		if (player.isCrouching() && onPlayerAttackedOrInteractedWithEntity(player, oldStack, entityType))
		{
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
		}
	}
	
	public static void onPlayerAttackEntity(AttackEntityEvent event)
	{
		Player player = event.getEntity();
		ItemStack oldStack = player.getMainHandItem();
		EntityType<?> entityType = event.getTarget().getType();
		onPlayerAttackedOrInteractedWithEntity(player, oldStack, entityType);
	}
	
	public static boolean onPlayerAttackedOrInteractedWithEntity(Player player, ItemStack oldStack, EntityType<?> entityType)
	{
		
		if (oldStack.is(Tags.Items.IMPRINTABLE) && ImprintedSlimeballItem.isEntityTypeValid(entityType))
		{
			if (!player.level.isClientSide)
			{
				oldStack.shrink(1);
				ItemStack newSlimeStack = ImprintedSlimeballItem.createItemStackForEntityType(entityType);
				if (!player.addItem(newSlimeStack))
				{
					player.drop(newSlimeStack, false);
				}
			}
			
			return true;
		}
		
		return false;
	}

	private static <T> DeferredRegister<T> defreg(IForgeRegistry<T> registry)
	{
		DeferredRegister<T> defreg = DeferredRegister.create(registry, MODID);
		defreg.register(FMLJavaModLoadingContext.get().getModEventBus());
		return defreg;
	}
}
