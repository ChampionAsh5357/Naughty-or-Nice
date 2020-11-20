package io.github.championash5357.naughtyornice.common.present;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.common.present.StructurePresent.Wrapper;
import io.github.championash5357.naughtyornice.common.util.EntityPos;
import io.github.championash5357.naughtyornice.common.util.Helper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants.BlockFlags;

public class StructurePresent extends Present<Wrapper> {

	public StructurePresent(Codec<Wrapper> codec) {
		super(codec);
	}

	@Override
	public boolean give(ServerPlayerEntity player, Wrapper config, BlockPos presentPos) {
		ServerWorld world = player.getServerWorld();
		Template template;
		try {
			template = world.getStructureTemplateManager().getTemplate(config.name);
		} catch (ResourceLocationException resourcelocationexception) {
            return false;
        }
		if(template == null) return false;
		BlockState state = world.getBlockState(presentPos);
		world.notifyBlockUpdate(presentPos, state, state, BlockFlags.DEFAULT);
		PlacementSettings settings = (new PlacementSettings()).setMirror(config.mirror).setRotation(config.rotation).setIgnoreEntities(config.ignoreEntities).setChunk((ChunkPos) null);
		if(config.integrity < 1.0f) settings.clearProcessors().addProcessor(new IntegrityProcessor(MathHelper.clamp(config.integrity, 0.0F, 1.0F))).setRandom(Helper.RANDOM);
		template.func_237144_a_(world, presentPos, settings, Helper.RANDOM);
		return true;
	}
	
	public static class Wrapper {
		
		public static final Codec<Wrapper> CODEC = RecordCodecBuilder.create(builder -> {
			return builder.group(ResourceLocation.CODEC.fieldOf("name").forGetter(inst -> inst.name),
					EntityPos.CODEC.codec().optionalFieldOf("player_position").forGetter(inst -> inst.playerPos),
					Codec.STRING.xmap(str -> Mirror.valueOf(str.toUpperCase()), mirror -> mirror.toString().toLowerCase()).optionalFieldOf("mirror", Mirror.NONE).forGetter(inst -> inst.mirror),
					Codec.STRING.xmap(str -> Rotation.valueOf(str.toUpperCase()), rot -> rot.toString().toLowerCase()).optionalFieldOf("rotation", Rotation.NONE).forGetter(inst -> inst.rotation),
					Codec.BOOL.optionalFieldOf("ignore_entities", false).forGetter(inst -> inst.ignoreEntities),
					Codec.floatRange(0.0f, 1.0f).optionalFieldOf("integrity", 1.0f).forGetter(inst -> inst.integrity))
					.apply(builder, Wrapper::new);
		});
		private final ResourceLocation name;
		private final Optional<EntityPos> playerPos;
		private final Mirror mirror;
		private final Rotation rotation;
		private final boolean ignoreEntities;
		private final float integrity;
		
		public Wrapper(final ResourceLocation name, final Optional<EntityPos> playerPos, final Mirror mirror, final Rotation rotation, final boolean ignoreEntities, final float integrity) {
			this.name = name;
			this.playerPos = playerPos;
			this.mirror = mirror;
			this.rotation = rotation;
			this.ignoreEntities = ignoreEntities;
			this.integrity = integrity;
		}
	}
}
