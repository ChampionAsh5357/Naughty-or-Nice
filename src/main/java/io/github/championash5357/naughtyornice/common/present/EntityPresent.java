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

import java.util.List;

import com.mojang.serialization.*;

import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.common.util.EntityInformation;
import net.minecraft.entity.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class EntityPresent extends Present<List<EntityInformation>> {
	
	public EntityPresent(Codec<List<EntityInformation>> codec) {
		super(codec);
	}

	@Override
	public DataResult<Present<List<EntityInformation>>> give(ServerPlayerEntity player, List<EntityInformation> config, BlockPos presentPos) {
		ServerWorld world = player.getServerWorld();
		for(EntityInformation info : config) {
			CompoundNBT nbt = info.getNbt().copy();
			boolean empty = nbt.isEmpty();
			nbt.putString("id", info.getType().getRegistryName().toString());
			Entity entity = EntityType.loadEntityAndExecute(nbt, world, spawnedEntity -> {
				info.getPos().applyPositionAndRotation(spawnedEntity, presentPos);
				return spawnedEntity;
			});
			
			if(entity == null) return DataResult.error("The following entity does not exist: " + info.getType().getRegistryName().toString(), this, Lifecycle.stable());
			else {
				if(empty && entity instanceof MobEntity)
					((MobEntity)entity).onInitialSpawn(world, world.getDifficultyForLocation(entity.getPosition()), SpawnReason.COMMAND, (ILivingEntityData)null, (CompoundNBT)null);
				
				if(!world.func_242106_g(entity)) return DataResult.error("Two entities have the same unique identifier.", this, Lifecycle.stable());
			}
		}
		return DataResult.success(this, Lifecycle.stable());
	}
}
