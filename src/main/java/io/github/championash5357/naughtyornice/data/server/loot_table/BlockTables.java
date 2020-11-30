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
