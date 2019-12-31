package com.github.commoble.entitydetectors;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Registrator<T extends IForgeRegistryEntry<T>>
{
	public final IForgeRegistry<T> registry;
	
	public Registrator(IForgeRegistry<T> registry)
	{
		this.registry = registry;
	}
	
	public T register(String registryKey, T entry)
	{
		return this.register(new ResourceLocation(EntityDetectors.MODID, registryKey), entry);
	}
	
	public T register(ResourceLocation loc, T entry)
	{
		entry.setRegistryName(loc);
		this.registry.register(entry);
		return entry;
	}
}