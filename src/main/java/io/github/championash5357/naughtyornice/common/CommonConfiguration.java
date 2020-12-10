package io.github.championash5357.naughtyornice.common;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class CommonConfiguration {

	public final DoubleValue chunkCheck;
	public final IntValue chunkChances;
	
	public CommonConfiguration(final ForgeConfigSpec.Builder builder) {
		this.chunkCheck = builder.comment("Sets the chance of a present spawning in a specified chunk around the player.",
				"Must be between 0 and 1.",
				"Defaults to 0.5.")
				.defineInRange("chunkCheck", 0.5, 0, 1);
		
		this.chunkChances = builder.comment("Sets the number of times a present will attempt to spawn in the given chunk.",
				"Must be between 0 and 256.",
				"Defaults to 64.")
				.defineInRange("chunkChances", 64, 0, 256);
	}

	public static final ForgeConfigSpec COMMON_SPEC;
	public static final CommonConfiguration COMMON;
	
	static {
		final Pair<CommonConfiguration, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfiguration::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}
}
