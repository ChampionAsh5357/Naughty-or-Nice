/*
 * Naughty or Nice
 * Copyright (C) 2020 ChampionAsh5357
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation version 3.0 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.championash5357.naughtyornice.common.present;

import com.mojang.serialization.*;

import io.github.championash5357.naughtyornice.api.present.Present;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandPresent extends Present<String> {

	public CommandPresent(Codec<String> codec) {
		super(codec);
	}

	@Override
	public DataResult<Present<String>> give(ServerPlayerEntity player, String config, BlockPos presentPos) {
		MinecraftServer server = player.server;
		String player_name = player.getGameProfile().getName();
		if(player_name == null) return DataResult.error("The player name is null.", this, Lifecycle.stable());
		return server.getCommandManager().handleCommand(server.getCommandSource(), "execute as " + player_name + " at " + player_name + " run " + config) == 0 ?
				DataResult.error("An exception has occured for the following command: " + config, this, Lifecycle.stable()) : 
					DataResult.success(this, Lifecycle.stable());
	}

}
