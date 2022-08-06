package commoble.entitydetectors;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public record CommonConfig(IntValue refreshRate)
{
	public static CommonConfig create(ForgeConfigSpec.Builder builder)
	{
		return new CommonConfig(
			builder.comment("How often entity detectors will check for entities (in ticks per update)")
				.defineInRange("refresh_rate", 10, 1, Integer.MAX_VALUE)
		);
	}
}
