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

package io.github.championash5357.naughtyornice.common.util;

import java.util.Objects;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.ashlib.serialization.CodecHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class StackInformation {

	public static final Codec<StackInformation> CODEC = RecordCodecBuilder.create(builder -> {
		return builder.group(CodecHelper.registryObject(ForgeRegistries.ITEMS).fieldOf("item").forGetter(inst -> inst.item),
				CompoundNBT.CODEC.optionalFieldOf("tag", null).forGetter(inst -> inst.tag))
				.apply(builder, StackInformation::new);
	});
	private final Item item;
	@Nullable
	private final CompoundNBT tag;
	
	public StackInformation(final ItemStack stack) {
		this(stack.getItem(), stack.getShareTag());
	}
	
	public StackInformation(final Item item, @Nullable final CompoundNBT tag) {
		if(item == null) throw new IllegalArgumentException("Item cannot be null.");
		this.item = item;
		this.tag = tag == null ? null : tag.copy();
	}
	
	public ResourceLocation getId() {
		return this.item.getRegistryName();
	}

	private boolean test(ItemStack stack) {
		return this.item == stack.getItem() && this.areShareTagsEqual(stack.getShareTag());
	}
	
	private boolean areShareTagsEqual(@Nullable CompoundNBT nbt) {
		if(this.tag == null) return nbt == null;
		else return nbt != null && this.tag.equals(nbt);
	}
	
	@Override
	public int hashCode() {
		return this.tag == null ? this.item.hashCode() : Objects.hash(this.item.hashCode(), this.tag.hashCode());
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof ItemStack) return this.test((ItemStack) o);
		else if(o instanceof StackInformation) return this.item.equals(((StackInformation) o).item) && this.tag.equals(((StackInformation) o).tag);
		else return super.equals(o);
	}
	
	@Override
	public String toString() {
		return "StackInformation[" + this.item + ", " + (this.tag == null ? "empty" : this.tag) + "]";
	}
}
