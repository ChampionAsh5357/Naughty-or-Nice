package io.github.championash5357.naughtyornice.common.present;

import java.util.List;

import com.mojang.serialization.Codec;

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
	public boolean give(ServerPlayerEntity player, List<EntityInformation> config, BlockPos presentPos) {
		ServerWorld world = player.getServerWorld();
		for(EntityInformation info : config) {
			CompoundNBT nbt = info.getNbt().copy();
			boolean empty = nbt.isEmpty();
			nbt.putString("id", info.getType().getRegistryName().toString());
			Entity entity = EntityType.loadEntityAndExecute(nbt, world, spawnedEntity -> {
				info.getPos().applyPositionAndRotation(spawnedEntity, presentPos);
				return spawnedEntity;
			});
			
			if(entity == null) return false;
			else {
				if(empty && entity instanceof MobEntity)
					((MobEntity)entity).onInitialSpawn(world, world.getDifficultyForLocation(entity.getPosition()), SpawnReason.COMMAND, (ILivingEntityData)null, (CompoundNBT)null);
				
				if(!world.func_242106_g(entity)) return false;
			}
		}
		return true;
	}
}
