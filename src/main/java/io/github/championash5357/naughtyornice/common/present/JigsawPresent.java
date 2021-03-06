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

import java.util.*;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.api.util.Helper;
import io.github.championash5357.naughtyornice.common.present.JigsawPresent.Wrapper;
import io.github.championash5357.naughtyornice.common.util.EntityPos;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.server.ServerWorld;

public class JigsawPresent extends Present<Wrapper> {

	public JigsawPresent(Codec<Wrapper> codec) {
		super(codec);
	}

	@Override
	public DataResult<Present<Wrapper>> give(ServerPlayerEntity player, Wrapper config, BlockPos presentPos) {
		ServerWorld world = player.getServerWorld();
		ChunkGenerator generator = world.getChunkProvider().getChunkGenerator();
		StructureManager manager = world.func_241112_a_();
		DynamicRegistries registries = world.func_241828_r();
		List<AbstractVillagePiece> components = new ArrayList<>();
		BlockPos pos = config.offset.isPresent() ? presentPos.add(config.offset.get()) : presentPos;
		JigsawManager.func_242837_a(registries, new VillageConfig(() -> registries.getRegistry(Registry.JIGSAW_POOL_KEY).getOrDefault(config.start_pool), config.size), AbstractVillagePiece::new, generator, world.getStructureTemplateManager(), pos, components, Helper.RANDOM, config.intersectingJigsawPieces, config.placeAtWorldHeight);
		components.forEach(piece -> piece.func_237001_a_(world, manager, generator, Helper.RANDOM, MutableBoundingBox.func_236990_b_(), pos, false));
		config.playerPos.ifPresent(e -> e.applyPositionAndRotation(player, pos));
		return DataResult.success(this, Lifecycle.stable());
	}
	
	public static class Wrapper {
		
		public static final Codec<Wrapper> CODEC = RecordCodecBuilder.create(builder -> {
			return builder.group(ResourceLocation.CODEC.fieldOf("start_pool").forGetter(wrapper -> wrapper.start_pool),
					Codec.intRange(0, 255).fieldOf("size").forGetter(wrapper -> wrapper.size),
					EntityPos.CODEC.codec().optionalFieldOf("player_position").forGetter(wrapper -> wrapper.playerPos),
					BlockPos.CODEC.optionalFieldOf("offset").forGetter(inst -> inst.offset),
					Codec.BOOL.optionalFieldOf("intersecting_jigsaw_pieces", true).forGetter(wrapper -> wrapper.intersectingJigsawPieces),
					Codec.BOOL.optionalFieldOf("place_world_height", false).forGetter(wrapper -> wrapper.placeAtWorldHeight))
					.apply(builder, Wrapper::new);
		});
		private final ResourceLocation start_pool;
		private final int size;
		private final Optional<EntityPos> playerPos;
		private final Optional<BlockPos> offset;
		private final boolean intersectingJigsawPieces;
		private final boolean placeAtWorldHeight;
		
		public Wrapper(final ResourceLocation start_pool, final int size, final Optional<EntityPos> playerPos, final Optional<BlockPos> offset, final boolean intersectingJigsawPieces, final boolean placeAtWorldHeight) {
			this.start_pool = start_pool;
			this.size = size;
			this.playerPos = playerPos;
			this.offset = offset;
			this.intersectingJigsawPieces = intersectingJigsawPieces;
			this.placeAtWorldHeight = placeAtWorldHeight;
		}
	}
}
