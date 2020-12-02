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

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.common.present.StructurePresent.Wrapper;
import io.github.championash5357.naughtyornice.common.util.EntityPos;
import io.github.championash5357.naughtyornice.common.util.Helper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants.BlockFlags;

public class StructurePresent extends Present<Wrapper> {

	private static final ResourceLocation EMPTY = new ResourceLocation("empty");

	public StructurePresent(Codec<Wrapper> codec) {
		super(codec);
	}

	@Override
	public DataResult<Present<Wrapper>> give(ServerPlayerEntity player, Wrapper config, BlockPos presentPos) {
		ServerWorld world = player.getServerWorld();
		BlockPos pos = config.offset.isPresent() ? presentPos.add(config.offset.get()) : presentPos;
		if(!config.name.equals(EMPTY)) {
			Template template;
			try {
				template = world.getStructureTemplateManager().getTemplate(config.name);
			} catch (ResourceLocationException resourcelocationexception) {
				return DataResult.error("Tried to load improperly formatted template: " + config.name, this, Lifecycle.stable());
			}
			if(template == null) return DataResult.error("A template does not exist for this name: " + config.name, this, Lifecycle.stable());
			BlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, BlockFlags.DEFAULT);
			PlacementSettings settings = (new PlacementSettings()).setMirror(config.mirror).setRotation(config.rotation).setIgnoreEntities(config.ignoreEntities).setChunk((ChunkPos) null);
			settings.clearProcessors().setRandom(Helper.RANDOM);
			config.processors.map(loc -> world.func_241828_r().getRegistry(Registry.STRUCTURE_PROCESSOR_LIST_KEY).getOrDefault(loc), sup -> sup.get()).func_242919_a().forEach(settings::addProcessor);
			template.func_237144_a_(world, pos, settings, Helper.RANDOM);
		}
		config.playerPos.ifPresent(e -> e.applyPositionAndRotation(player, pos));
		return DataResult.success(this, Lifecycle.stable());
	}

	public static class Wrapper {

		public static final Codec<Wrapper> CODEC = RecordCodecBuilder.create(builder -> {
			return builder.group(ResourceLocation.CODEC.fieldOf("name").forGetter(inst -> inst.name),
					EntityPos.CODEC.codec().optionalFieldOf("player_position").forGetter(inst -> inst.playerPos),
					BlockPos.CODEC.optionalFieldOf("offset").forGetter(inst -> inst.offset),
					Codec.STRING.xmap(str -> Mirror.valueOf(str.toUpperCase()), mirror -> mirror.toString().toLowerCase()).optionalFieldOf("mirror", Mirror.NONE).forGetter(inst -> inst.mirror),
					Codec.STRING.xmap(str -> Rotation.valueOf(str.toUpperCase()), rot -> rot.toString().toLowerCase()).optionalFieldOf("rotation", Rotation.NONE).forGetter(inst -> inst.rotation),
					Codec.BOOL.optionalFieldOf("ignore_entities", false).forGetter(inst -> inst.ignoreEntities),
					Codec.either(ResourceLocation.CODEC, IStructureProcessorType.field_242921_l.xmap(spl -> Helper.supplierFunction(spl), sup -> sup.get())).optionalFieldOf("processors", Either.left(EMPTY)).forGetter(inst -> inst.processors))
					.apply(builder, Wrapper::new);
		});
		private final ResourceLocation name;
		private final Optional<EntityPos> playerPos;
		private final Optional<BlockPos> offset;
		private final Mirror mirror;
		private final Rotation rotation;
		private final boolean ignoreEntities;
		private final Either<ResourceLocation, Supplier<StructureProcessorList>> processors;

		public Wrapper(final ResourceLocation name, final Optional<EntityPos> playerPos, final Optional<BlockPos> offset, final Mirror mirror, final Rotation rotation, final boolean ignoreEntities, final Either<ResourceLocation, Supplier<StructureProcessorList>> processors) {
			this.name = name;
			this.playerPos = playerPos;
			this.offset = offset;
			this.mirror = mirror;
			this.rotation = rotation;
			this.ignoreEntities = ignoreEntities;
			this.processors = processors;
		}
	}
}
