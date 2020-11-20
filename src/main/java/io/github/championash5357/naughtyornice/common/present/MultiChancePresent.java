package io.github.championash5357.naughtyornice.common.present;

import java.util.Map;

import com.mojang.serialization.Codec;

import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.api.present.WrappedPresent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class MultiChancePresent extends Present<Map<WrappedPresent<?, ?>, Double>> {

	public MultiChancePresent(Codec<Map<WrappedPresent<?, ?>, Double>> codec) {
		super(codec);
	}

	@Override
	public boolean give(ServerPlayerEntity player, Map<WrappedPresent<?, ?>, Double> config, BlockPos presentPos) {
		if(config.isEmpty()) return false;
		config.entrySet().stream().filter(entry -> Math.random() < entry.getValue()).forEach(entry -> entry.getKey().give(player, presentPos));
		return true;
	}
}
