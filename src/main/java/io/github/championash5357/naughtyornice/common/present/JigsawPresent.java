package io.github.championash5357.naughtyornice.common.present;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.common.present.JigsawPresent.Wrapper;
import io.github.championash5357.naughtyornice.common.util.EntityPos;
import io.github.championash5357.naughtyornice.common.util.Helper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.server.ServerWorld;

public class JigsawPresent extends Present<Wrapper> {

	public JigsawPresent(Codec<Wrapper> codec) {
		super(codec);
	}

	@Override
	public boolean give(ServerPlayerEntity player, Wrapper config, BlockPos presentPos) {
		ServerWorld world = player.getServerWorld();
		ChunkGenerator generator = world.getChunkProvider().getChunkGenerator();
		StructureManager manager = world.func_241112_a_();
		List<AbstractVillagePiece> components = new ArrayList<>();
		JigsawManager.func_242837_a(world.func_241828_r(), config.jigsawConfig, AbstractVillagePiece::new, generator, world.getStructureTemplateManager(), presentPos, components, Helper.RANDOM, config.intersectingJigsawPieces, config.placeAtWorldHeight);
		components.forEach(piece -> piece.func_237001_a_(world, manager, generator, Helper.RANDOM, MutableBoundingBox.func_236990_b_(), presentPos, false));
		config.playerPos.ifPresent(pos -> pos.applyPositionAndRotation(player, presentPos));
		return true;
	}
	
	public static class Wrapper {
		
		public static final MapCodec<VillageConfig> JIGSAW_CONFIG_CODEC = RecordCodecBuilder.mapCodec(builder -> {
			return builder.group(JigsawPattern.field_244392_b_.fieldOf("start_pool").forGetter(VillageConfig::func_242810_c),
					Codec.intRange(0, 255).fieldOf("size").forGetter(VillageConfig::func_236534_a_))
					.apply(builder, VillageConfig::new);
		});
		public static final Codec<Wrapper> CODEC = RecordCodecBuilder.create(builder -> {
			return builder.group(JIGSAW_CONFIG_CODEC.forGetter(wrapper -> wrapper.jigsawConfig),
					EntityPos.CODEC.codec().optionalFieldOf("player_position").forGetter(wrapper -> wrapper.playerPos),
					Codec.BOOL.optionalFieldOf("intersecting_jigsaw_pieces", true).forGetter(wrapper -> wrapper.intersectingJigsawPieces),
					Codec.BOOL.optionalFieldOf("place_world_height", false).forGetter(wrapper -> wrapper.placeAtWorldHeight))
					.apply(builder, Wrapper::new);
		});
		private final VillageConfig jigsawConfig;
		private final Optional<EntityPos> playerPos;
		private final boolean intersectingJigsawPieces;
		private final boolean placeAtWorldHeight;
		
		public Wrapper(final VillageConfig jigsawConfig, final Optional<EntityPos> playerPos, final boolean intersectingJigsawPieces, final boolean placeAtWorldHeight) {
			this.jigsawConfig = jigsawConfig;
			this.playerPos = playerPos;
			this.intersectingJigsawPieces = intersectingJigsawPieces;
			this.placeAtWorldHeight = placeAtWorldHeight;
		}
	}
}
