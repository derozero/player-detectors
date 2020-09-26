package com.github.commoble.entitydetectors.blocks;

import com.github.commoble.entitydetectors.EntityDetectors;
import com.github.commoble.entitydetectors.Registrator;
import com.github.commoble.entitydetectors.RegistryNames;
import com.github.commoble.entitydetectors.ResourceLocations;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(EntityDetectors.MODID)
public class BlockRegistrar
{
	@ObjectHolder(RegistryNames.PLAYER_DETECTOR)
	public static final EntityDetectorBlock PLAYER_DETECTOR = null;
	
	@ObjectHolder(RegistryNames.MOB_DETECTOR)
	public static final EntityDetectorBlock MOB_DETECTOR = null;
	
	@ObjectHolder(RegistryNames.FAKE_SLIME)
	public static final Block FAKE_SLIME = null;
	
	public static void onRegisterBlocks(Registrator<Block> reg)
	{
		reg.register(ResourceLocations.PLAYER_DETECTOR, new PlayerDetectorBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F).lightValue(7)));
		reg.register(ResourceLocations.MOB_DETECTOR, new MobDetectorBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F).lightValue(7).func_226896_b_()));
		reg.register(ResourceLocations.FAKE_SLIME, new Block(Block.Properties.create(Material.CLAY)));
	}
}
