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

package io.github.championash5357.naughtyornice.common.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.DoubleStream;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.naughtyornice.common.codec.PrimitiveCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap.Type;

public class EntityPos {

	private static final Codec<Vector3d> VECTOR3D_CODEC = PrimitiveCodecs.DOUBLE_STREAM.comapFlatMap(stream -> {
		return validateDoubleStreamSize(stream, 3).map(coordinates -> {
			return new Vector3d(coordinates[0], coordinates[1], coordinates[2]);
		});
	}, vec -> {
		return DoubleStream.of(vec.x, vec.y, vec.z);
	}).stable();
	public static final MapCodec<EntityPos> CODEC = RecordCodecBuilder.mapCodec(builder -> {
		return builder.group(VECTOR3D_CODEC.fieldOf("position").forGetter(inst -> inst.pos),
				Codec.FLOAT.optionalFieldOf("yaw").forGetter(inst -> inst.yaw),
				Codec.FLOAT.optionalFieldOf("pitch").forGetter(inst -> inst.pitch),
				Codec.BOOL.optionalFieldOf("use_world_height", true).forGetter(inst -> inst.useWorldHeight))
				.apply(builder, EntityPos::new);
	});
	private final Vector3d pos;
	private final Optional<Float> yaw;
	private final Optional<Float> pitch;
	private final boolean useWorldHeight;

	public EntityPos(final BlockPos pos) {
		this(pos, Optional.empty(), Optional.empty(), true);
	}

	public EntityPos(final Vector3d pos) {
		this(pos, Optional.empty(), Optional.empty(), true);
	}
	
	public EntityPos(final BlockPos pos, final boolean useWorldHeight) {
		this(pos, Optional.empty(), Optional.empty(), useWorldHeight);
	}

	public EntityPos(final Vector3d pos, final boolean useWorldHeight) {
		this(pos, Optional.empty(), Optional.empty(), useWorldHeight);
	}

	public EntityPos(final BlockPos pos, final Optional<Float> yaw, final Optional<Float> pitch, final boolean useWorldHeight) {
		this(new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), yaw, pitch, useWorldHeight);
	}

	public EntityPos(final Vector3d pos, final Optional<Float> yaw, final Optional<Float> pitch, final boolean useWorldHeight) {
		this.pos = pos;
		this.yaw = yaw;
		this.pitch = pitch;
		this.useWorldHeight = useWorldHeight;
	}
	
	public void applyPositionAndRotation(Entity entity, BlockPos referencePos) {
		double x = referencePos.getX() + pos.x, z = referencePos.getZ() + pos.z;
		double offset = (referencePos.getX() == x && referencePos.getZ() == z) ? 1 : 0;
		double y = useWorldHeight ? entity.world.getHeight(Type.MOTION_BLOCKING, (int) x, (int) z) - offset : (referencePos.getY() + pos.y);
		if(entity instanceof ServerPlayerEntity) ((ServerPlayerEntity) entity).connection.setPlayerLocation(x, y, z, this.yaw.orElse(entity.rotationYaw), this.pitch.orElse(entity.rotationPitch));
		else entity.setPositionAndRotation(x, y, z, this.yaw.orElse(entity.rotationYaw), this.pitch.orElse(entity.rotationPitch));
	}

	private static DataResult<double[]> validateDoubleStreamSize(DoubleStream stream, int size) {
		double[] aint = stream.limit((long)(size + 1)).toArray();
		if (aint.length != size) {
			String s = "Input is not a list of " + size + " doubles";
			return aint.length >= size ? DataResult.error(s, Arrays.copyOf(aint, size)) : DataResult.error(s);
		} else {
			return DataResult.success(aint);
		}
	}
}
