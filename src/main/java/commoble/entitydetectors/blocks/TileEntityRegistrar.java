package commoble.entitydetectors.blocks;

import commoble.entitydetectors.EntityDetectors;
import commoble.entitydetectors.Registrator;
import commoble.entitydetectors.RegistryNames;
import commoble.entitydetectors.ResourceLocations;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(EntityDetectors.MODID)
public class TileEntityRegistrar
{
	@ObjectHolder(RegistryNames.MOB_DETECTOR)
	public static final TileEntityType<MobDetectorTileEntity> MOB_DETECTOR = null;

	public static void onRegisterTileEntities(Registrator<TileEntityType<?>> reg)
	{
		reg.register(ResourceLocations.MOB_DETECTOR, TileEntityType.Builder.create(MobDetectorTileEntity::new, BlockRegistrar.MOB_DETECTOR).build(null));
	}
}
