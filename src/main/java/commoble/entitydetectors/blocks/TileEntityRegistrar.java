package com.github.commoble.entitydetectors.blocks;

import com.github.commoble.entitydetectors.EntityDetectors;
import com.github.commoble.entitydetectors.Registrator;
import com.github.commoble.entitydetectors.RegistryNames;
import com.github.commoble.entitydetectors.ResourceLocations;

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
