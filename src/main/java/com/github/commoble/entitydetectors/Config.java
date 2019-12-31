package com.github.commoble.entitydetectors;

import com.github.commoble.entitydetectors.ConfigHelper.ConfigValueListener;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
	public ConfigValueListener<Integer> refreshRate;
	
	public Config(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber)
	{
		builder.push("Blocks");
		{
			this.refreshRate = subscriber.subscribe(builder
				.comment("How often entity detectors will check for entities (in ticks per update)")
				.translation("entitydetectors.refresh_rate")
				.defineInRange("refresh_rate", 10, 1, Integer.MAX_VALUE));
		}
	}
}
