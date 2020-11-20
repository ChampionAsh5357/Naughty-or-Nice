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

package io.github.championash5357.naughtyornice.data.client;

import io.github.championash5357.naughtyornice.common.block.PresentBlock;
import io.github.championash5357.naughtyornice.common.init.GeneralRegistrar;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BlockStateProvider {

	public BlockStates(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
		super(gen, modid, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		this.createBlock(GeneralRegistrar.PRESENT.get(), "block/present");
	}
	
	private void createBlock(Block block, String parentModel) {
		ModelFile presentModel = new ModelFile.ExistingModelFile(this.modLoc(parentModel), this.models().existingFileHelper);
		ModelFile presentModelOpen = new ModelFile.ExistingModelFile(this.modLoc(parentModel + "_open"), this.models().existingFileHelper);
		this.getVariantBuilder(block).partialState().with(PresentBlock.OPEN, false).modelForState().modelFile(presentModel).addModel()
		.partialState().with(PresentBlock.OPEN, true).modelForState().modelFile(presentModelOpen).addModel();
		this.simpleBlockItem(block, presentModel);
	}
}
