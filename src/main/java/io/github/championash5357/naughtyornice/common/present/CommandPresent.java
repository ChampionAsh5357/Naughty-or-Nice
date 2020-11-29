package io.github.championash5357.naughtyornice.common.present;

import com.mojang.serialization.Codec;

import io.github.championash5357.naughtyornice.api.present.Present;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandPresent extends Present<String> {

	public CommandPresent(Codec<String> codec) {
		super(codec);
	}

	@Override
	public boolean give(ServerPlayerEntity player, String config, BlockPos presentPos) {
		MinecraftServer server = player.server;
		String player_name = player.getGameProfile().getName();
		if(player_name == null) return false;
		server.getCommandManager().handleCommand(server.getCommandSource(), "execute as " + player_name + " at " + player_name + " run " + config);
		return true;
	}

}
