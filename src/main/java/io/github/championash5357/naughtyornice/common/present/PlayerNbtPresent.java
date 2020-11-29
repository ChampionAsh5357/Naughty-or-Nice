package io.github.championash5357.naughtyornice.common.present;

import com.mojang.serialization.Codec;

import io.github.championash5357.naughtyornice.api.present.Present;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class PlayerNbtPresent extends Present<CompoundNBT> {

	public PlayerNbtPresent(Codec<CompoundNBT> codec) {
		super(codec);
	}

	@Override
	public boolean give(ServerPlayerEntity player, CompoundNBT config, BlockPos presentPos) {
		player.deserializeNBT(player.serializeNBT().merge(config));
		return true;
	}
}
