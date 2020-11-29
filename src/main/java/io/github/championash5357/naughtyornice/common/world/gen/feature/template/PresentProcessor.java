package io.github.championash5357.naughtyornice.common.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.naughtyornice.common.init.GeneralRegistrar;
import io.github.championash5357.naughtyornice.common.util.Helper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import net.minecraft.world.gen.feature.template.Template.EntityInfo;

public class PresentProcessor extends StructureProcessor {

	public static final Codec<PresentProcessor> CODEC = RecordCodecBuilder.create(builder -> {
		return builder.group(Codec.INT.optionalFieldOf("min", -100).forGetter(inst -> inst.min),
				Codec.INT.optionalFieldOf("max", 100).forGetter(inst -> inst.max))
				.apply(builder, PresentProcessor::new);
	});
	private final int min, max;

	public PresentProcessor(final int min, final int max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public BlockInfo process(IWorldReader world, BlockPos piecePos, BlockPos seedPos, BlockInfo rawBlockInfo, BlockInfo blockInfo, PlacementSettings settings, Template template) {
		CompoundNBT nbt = blockInfo.nbt != null ? blockInfo.nbt : new CompoundNBT();
		nbt.putInt("niceness", Helper.RANDOM.nextInt(this.max - this.min + 1) + this.min);
		return new BlockInfo(blockInfo.pos, blockInfo.state, nbt);
	}
	
	@Override
	public EntityInfo processEntity(IWorldReader world, BlockPos seedPos, EntityInfo rawEntityInfo, EntityInfo entityInfo, PlacementSettings settings, Template template) {
		CompoundNBT nbt = entityInfo.nbt != null ? entityInfo.nbt : new CompoundNBT();
		nbt.putInt("niceness", Helper.RANDOM.nextInt(this.max - this.min + 1) + this.min);
		return new EntityInfo(entityInfo.pos, entityInfo.blockPos, nbt);
	}
	
	@Override
	protected IStructureProcessorType<?> getType() {
		return GeneralRegistrar.PROCESSOR;
	}
}
