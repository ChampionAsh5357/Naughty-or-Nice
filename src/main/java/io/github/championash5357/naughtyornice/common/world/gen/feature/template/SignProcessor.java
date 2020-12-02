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

import io.github.championash5357.ashlib.serialization.CodecHelper;
import io.github.championash5357.naughtyornice.api.present.PresentManager;
import io.github.championash5357.naughtyornice.common.init.GeneralRegistrar;
import io.github.championash5357.naughtyornice.common.util.Helper;
import net.minecraft.block.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import net.minecraftforge.registries.ForgeRegistries;

public class SignProcessor extends StructureProcessor {
	
	public static final Codec<SignProcessor> CODEC = CodecHelper.registryObject(ForgeRegistries.BLOCKS).xmap(SignProcessor::new, inst -> inst.block);
	private final Block block;
	
	private SignProcessor(final Block block) {
		this.block = block;
	}
	
	@Override
	public BlockInfo process(IWorldReader world, BlockPos piecePos, BlockPos seedPos, BlockInfo rawBlockInfo, BlockInfo blockInfo, PlacementSettings settings, Template template) {
		if(blockInfo.state.getBlock() == block || blockInfo.state.getBlock() instanceof AbstractSignBlock) {
			CompoundNBT nbt = blockInfo.nbt != null ? blockInfo.nbt : new CompoundNBT();
			PresentManager.getInstance().getSignInformation().ifPresent(info -> nbt.merge(info.toNBT()));
			return new BlockInfo(blockInfo.pos, blockInfo.state.getBlock() instanceof AbstractSignBlock ? blockInfo.state : Blocks.OAK_SIGN.getDefaultState().with(StandingSignBlock.ROTATION, Helper.RANDOM.nextInt(16)), nbt);
		} else return blockInfo;
	}
	
	@Override
	protected IStructureProcessorType<?> getType() {
		return GeneralRegistrar.SIGN_PROCESSOR.get();
	}
}
