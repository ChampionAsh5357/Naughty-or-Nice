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

package io.github.championash5357.naughtyornice.common.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.naughtyornice.common.block.PresentBlock;
import io.github.championash5357.naughtyornice.common.init.GeneralRegistrar;
import io.github.championash5357.naughtyornice.common.util.Helper;
import net.minecraft.entity.EntityType;
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
		if(blockInfo.state.getBlock() instanceof PresentBlock) {
			CompoundNBT nbt = blockInfo.nbt != null ? blockInfo.nbt : new CompoundNBT();
			nbt.putInt("niceness", Helper.RANDOM.nextInt(this.max - this.min + 1) + this.min);
			return new BlockInfo(blockInfo.pos, blockInfo.state, nbt);
		} else return blockInfo;
	}

	@Override
	public EntityInfo processEntity(IWorldReader world, BlockPos seedPos, EntityInfo rawEntityInfo, EntityInfo entityInfo, PlacementSettings settings, Template template) {
		CompoundNBT nbt = entityInfo.nbt != null ? entityInfo.nbt : new CompoundNBT();
		if(nbt.getString("id").equals(EntityType.FALLING_BLOCK.getRegistryName().toString())) {
			CompoundNBT teData = nbt.contains("TileEntityData") ? nbt.getCompound("TileEntityData") : new CompoundNBT();
			teData.putInt("niceness", Helper.RANDOM.nextInt(this.max - this.min + 1) + this.min);
			nbt.put("TileEntityData", teData);
			return new EntityInfo(entityInfo.pos, entityInfo.blockPos, nbt);
		}
		return entityInfo;
	}

	@Override
	protected IStructureProcessorType<?> getType() {
		return GeneralRegistrar.PRESENT_PROCESSOR.get();
	}
}
