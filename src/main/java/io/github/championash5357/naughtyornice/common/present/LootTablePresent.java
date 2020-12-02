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

import java.util.Collection;
import java.util.stream.IntStream;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.common.present.LootTablePresent.Wrapper;
import io.github.championash5357.naughtyornice.common.util.Helper;
import io.github.championash5357.naughtyornice.common.util.LootSpawnLocation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.*;
import net.minecraft.loot.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class LootTablePresent extends Present<Wrapper> {

	private static Collection<Item> items;

	public LootTablePresent(Codec<Wrapper> codec) {
		super(codec);
	}

	@Override
	public DataResult<Present<Wrapper>> give(ServerPlayerEntity player, Wrapper config, BlockPos presentPos) {
		ServerWorld world = player.getServerWorld();
		Inventory inv = new Inventory(Helper.RANDOM.nextInt(config.size) + 1);
		world.getServer().getLootTableManager().getLootTableFromLocation(config.lootTable)
		.fillInventory(inv, (new LootContext.Builder(world))
				.withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(presentPos))
				.withSeededRandom(0L, Helper.RANDOM)
				.withLuck(player.getLuck())
				.withParameter(LootParameters.THIS_ENTITY, player)
				.build(LootParameterSets.CHEST));
		switch(config.lootSpawnLocation) {
		case GROUND:
			InventoryHelper.dropInventoryItems(player.world, presentPos, inv);
			return DataResult.success(this, Lifecycle.stable());
		case RANDOM_GROUND:
			if(items == null) items = ForgeRegistries.ITEMS.getValues();
			IntStream.range(0, Helper.RANDOM.nextInt(config.size) + 1).forEach(i -> {
				NonNullList<ItemStack> stacks = NonNullList.create();
				items.stream().skip(Helper.RANDOM.nextInt(items.size())).findFirst().get().fillItemGroup(ItemGroup.SEARCH, stacks);
				ItemStack stack = stacks.get(Helper.RANDOM.nextInt(stacks.size())).copy();
				stack.setCount(Math.min(Helper.RANDOM.nextInt(64) + 1, stack.getMaxStackSize()));
				InventoryHelper.spawnItemStack(world, presentPos.getX(), presentPos.getY(), presentPos.getZ(), stack);
			});
			return DataResult.success(this, Lifecycle.stable());
		case INVENTORY:
			boolean drop = false;
			for(ItemStack stack : inv.func_233543_f_()) {
				if(drop) {
					InventoryHelper.spawnItemStack(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), stack);
				} else if(!player.addItemStackToInventory(stack)) drop = true;
			}
			return DataResult.success(this, Lifecycle.stable());
		case RANDOM_INVENTORY:
			if(items == null) items = ForgeRegistries.ITEMS.getValues();
			boolean isFull = false;
			for(int i = 0; i < Helper.RANDOM.nextInt(config.size) + 1; i++) {
				NonNullList<ItemStack> stacks = NonNullList.create();
				items.stream().skip(Helper.RANDOM.nextInt(items.size())).findFirst().get().fillItemGroup(ItemGroup.SEARCH, stacks);
				ItemStack stack = stacks.get(Helper.RANDOM.nextInt(stacks.size())).copy();
				stack.setCount(Math.min(Helper.RANDOM.nextInt(64) + 1, stack.getMaxStackSize()));
				if(isFull) {
					InventoryHelper.spawnItemStack(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), stack);
				} else if(!player.addItemStackToInventory(stack)) isFull = true;
			}
			return DataResult.success(this, Lifecycle.stable());
		default:
			return DataResult.error("The specified loot spawn location is not valid: " + config.lootSpawnLocation.getString(), this, Lifecycle.stable());
		}
	}

	public static class Wrapper {

		public static final Codec<Wrapper> CODEC = RecordCodecBuilder.create(builder -> {
			return builder.group(ResourceLocation.CODEC.fieldOf("name").forGetter(inst -> inst.lootTable),
					Codec.INT.optionalFieldOf("size", 1).forGetter(inst -> inst.size),
					Codec.STRING.xmap(LootSpawnLocation::getValue, LootSpawnLocation::getString).optionalFieldOf("loot_spawn_location", LootSpawnLocation.GROUND).forGetter(inst -> inst.lootSpawnLocation))
					.apply(builder, Wrapper::new);
		});

		private final ResourceLocation lootTable;
		private final int size;
		private final LootSpawnLocation lootSpawnLocation;

		public Wrapper(final ResourceLocation lootTable, final int size, final LootSpawnLocation lootSpawnLocation) {
			this.lootTable = lootTable;
			this.size = size;
			this.lootSpawnLocation = lootSpawnLocation;
		}
	}
}
