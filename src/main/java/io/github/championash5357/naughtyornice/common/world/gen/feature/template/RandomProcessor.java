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

import java.util.Collection;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.ashlib.serialization.CodecHelper;
import io.github.championash5357.naughtyornice.api.util.Helper;
import io.github.championash5357.naughtyornice.common.init.GeneralRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import net.minecraftforge.registries.ForgeRegistries;

public class RandomProcessor extends StructureProcessor {
	
	public static final Codec<RandomProcessor> CODEC = RecordCodecBuilder.create(builder -> {
		return builder.group(CodecHelper.registryObject(ForgeRegistries.BLOCKS).fieldOf("block").forGetter(inst -> inst.block),
				Codec.doubleRange(0.0, 1.0).optionalFieldOf("chance", 1.0).forGetter(inst -> inst.chance))
				.apply(builder, RandomProcessor::new);
	});
	private static Collection<Block> blocks;
	private final Block block;
	private final double chance;
	
	private RandomProcessor(final Block block, final double chance) {
		this.block = block;
		this.chance = chance;
	}
	
	@Override
	public BlockInfo process(IWorldReader world, BlockPos piecePos, BlockPos seedPos, BlockInfo rawBlockInfo, BlockInfo blockInfo, PlacementSettings settings, Template template) {
		if(blockInfo.state.getBlock() == this.block && Helper.RANDOM.nextDouble() < this.chance) {
			if(blocks == null) blocks = ForgeRegistries.BLOCKS.getValues();
			List<BlockState> states = blocks.stream().skip(Helper.RANDOM.nextInt(blocks.size())).findFirst().get().getStateContainer().getValidStates();
			return new BlockInfo(blockInfo.pos, states.get(Helper.RANDOM.nextInt(states.size())), blockInfo.nbt);
		} else return blockInfo;
	}
	
	@Override
	protected IStructureProcessorType<?> getType() {
		return GeneralRegistrar.RANDOM_PROCESSOR.get();
	}
}
