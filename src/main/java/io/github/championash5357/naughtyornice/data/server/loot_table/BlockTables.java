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

package io.github.championash5357.naughtyornice.data.server.loot_table;

import com.google.common.collect.ImmutableList;

import io.github.championash5357.naughtyornice.common.init.GeneralRegistrar;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;

public class BlockTables extends BlockLootTables {

	@Override
	protected void addTables() {
		this.registerLootTable(GeneralRegistrar.PRESENT.get(), block -> {
			return LootTable.builder().addLootPool(withSurvivesExplosion(block,
					LootPool.builder()
					.rolls(ConstantRange.of(1))
					.addEntry(ItemLootEntry.builder(block)
							.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
							.acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
									.replaceOperation("niceness", "BlockEntityTag.niceness")))));
		});
	}
	
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ImmutableList.of(GeneralRegistrar.PRESENT.get());
	}
}
