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

package io.github.championash5357.naughtyornice.api.util;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

/**
 * Deserializes sign information into a readable format
 * for json lines.
 */
public class SignInformation {

	/**
	 * The sign information codec.
	 */
	public static final Codec<SignInformation> CODEC = RecordCodecBuilder.create(builder -> {
		return builder.group(Codec.STRING.xmap(str -> DyeColor.byTranslationKey(str, DyeColor.BLACK), DyeColor::getTranslationKey).optionalFieldOf("color", DyeColor.BLACK).forGetter(inst -> inst.color),
				GeneralCodecs.TEXT_COMPONENT_CODEC.listOf().flatXmap(list -> {
					if(list.size() > 4) return DataResult.error("There are more than four text components to display on the sign.", list.toArray(new ITextComponent[0]));
					return DataResult.success(list.toArray(new ITextComponent[0]));
				}, array -> {
					return array.length != 4 ? DataResult.error("There are not four text components to encode.", Lists.newArrayList(array)) : DataResult.success(Lists.newArrayList(array));
				}).fieldOf("text").forGetter(inst -> inst.text))
				.apply(builder, SignInformation::new);
	});

	private final DyeColor color;
	private final ITextComponent[] text;

	/**
	 * A simple constructor. Should only be referenced
	 * through the codec. Left public for data generators.
	 * 
	 * @param color The text color
	 * @param text The sign text
	 */
	public SignInformation(final DyeColor color, final ITextComponent[] text) {
		if(text.length > 4) throw new IllegalArgumentException("A sign should only have at most four text components.");
		this.text = new ITextComponent[4];
		this.color = color;
		this.expandTextArray(text);
	}

	private void expandTextArray(final ITextComponent[] signText) {
		for(int i = 0; i < this.text.length; i++) {
			if(i < signText.length) this.text[i] = signText[i].deepCopy();
			else this.text[i] = StringTextComponent.EMPTY;
		}
	}

	/**
	 * Attaches the sign information directly
	 * to the tile entity.
	 * 
	 * @param te The tile entity
	 */
	public void attach(final SignTileEntity te) {
		te.setTextColor(this.color);
		for(int i = 0; i < this.text.length; i++) te.setText(i, this.text[i]);
	}

	/**
	 * Converts the information into nbt data.
	 * 
	 * @return A {@link CompoundNBT} of the data
	 */
	public CompoundNBT toNBT() {
		CompoundNBT nbt = new CompoundNBT();
		for(int i = 0; i < 4; ++i) nbt.putString("Text" + (i + 1), ITextComponent.Serializer.toJson(this.text[i]));
		nbt.putString("Color", this.color.getTranslationKey());
		return nbt;
	}
}
