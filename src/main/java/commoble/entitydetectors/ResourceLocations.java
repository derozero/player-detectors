package commoble.entitydetectors;

import net.minecraft.util.ResourceLocation;

public class ResourceLocations
{
	public static final ResourceLocation PLAYER_DETECTOR = getModRL(RegistryNames.PLAYER_DETECTOR);
	public static final ResourceLocation MOB_DETECTOR = getModRL(RegistryNames.MOB_DETECTOR);
	public static final ResourceLocation IMPRINTED_SLIME = getModRL(RegistryNames.IMPRINTED_SLIME_BALL);
	public static final ResourceLocation FAKE_SLIME = getModRL(RegistryNames.FAKE_SLIME);
	
	public static final ResourceLocation SLIME_TAG = getModRL("mob_detector_filters");

	public static ResourceLocation getModRL(String name)
	{
		return new ResourceLocation(EntityDetectors.MODID, name);
	}
}
