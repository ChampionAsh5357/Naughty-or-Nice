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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.ashlib.util.CodecHelper;
import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.common.codec.NonNullListCodec;
import io.github.championash5357.naughtyornice.common.present.ItemStackPresent.Wrapper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemStackPresent extends Present<Wrapper> {

	public ItemStackPresent(Codec<Wrapper> codec) {
		super(codec);
	}

	@Override
	public boolean give(ServerPlayerEntity player, Wrapper config, BlockPos presentPos) {
		switch(config.transferType) {
		case GROUND:
			InventoryHelper.dropItems(player.world, presentPos, config.stacks);
			return true;
		case INVENTORY:
			boolean drop = false;
			for(ItemStack stack : config.stacks) {
				if(drop) {
					InventoryHelper.spawnItemStack(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), stack);
				} else if(!player.addItemStackToInventory(stack)) drop = true;
			}
			return true;
		default:
			return false;
		}
	}
	
	public static class Wrapper {
		
		private static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create(builder -> {
			return builder.group(CodecHelper.createRegistryObjectCodec(ForgeRegistries.ITEMS).fieldOf("item").forGetter(ItemStack::getItem),
					Codec.INT.fieldOf("count").forGetter(ItemStack::getCount),
					CompoundNBT.CODEC.optionalFieldOf("nbt", null).forGetter(ItemStack::getTag))
					.apply(builder, ItemStack::new);
		});
		public static final Codec<Wrapper> CODEC = RecordCodecBuilder.create(builder -> {
			return builder.group(new NonNullListCodec<>(ITEM_STACK_CODEC).fieldOf("stacks").forGetter(wrapper -> wrapper.stacks),
					Codec.STRING.xmap(Type::valueOf, Type::getString).optionalFieldOf("type", Type.GROUND).forGetter(wrapper -> wrapper.transferType))
					.apply(builder, Wrapper::new);
		});
		private final NonNullList<ItemStack> stacks;
		private final Type transferType;
		
		public Wrapper(NonNullList<ItemStack> stacks, Type type) {
			this.stacks = stacks;
			this.transferType = type;
		}
		
		public static enum Type implements IStringSerializable {
			GROUND,
			INVENTORY;

			@Override
			public String getString() {
				return this.toString().toLowerCase();
			}
		}
	}
}
