package commoble.entitydetectors;
import commoble.entitydetectors.registrables.EntityDetectorBlock;
import commoble.entitydetectors.registrables.MobDetectorBlock;
import commoble.entitydetectors.registrables.MobDetectorBlockEntity;
import commoble.entitydetectors.registrables.PlayerDetectorBlock;
import commoble.entitydetectors.registrables.PlayerDetectorBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

@Mod(EntityDetectors.MODID)
public class EntityDetectors
{
	public static final String MODID = "entitydetectors";
	
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

	public static final RegistryObject<BlockEntityType<PlayerDetectorBlockEntity>> PLAYER_DETECTOR_BET = BETS.register(Names.PLAYER_DETECTOR, () -> BlockEntityType.Builder.of(PlayerDetectorBlockEntity::create, PLAYER_DETECTOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<MobDetectorBlockEntity>> MOB_DETECTOR_BET = BETS.register(Names.MOB_DETECTOR, () -> BlockEntityType.Builder.of(MobDetectorBlockEntity::create, MOB_DETECTOR.get()).build(null));
	
	public EntityDetectors()
	{		
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		
	}
	
	private static <T> DeferredRegister<T> defreg(IForgeRegistry<T> registry)
	{
		DeferredRegister<T> defreg = DeferredRegister.create(registry, MODID);
		defreg.register(FMLJavaModLoadingContext.get().getModEventBus());
		return defreg;
	}
}
