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

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.*;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;

/**
 * A helper for book information to make it look nicer.
 */
public class BookInformation {

	/**
	 * The book information codec.
	 */
	public static final Codec<BookInformation> CODEC = RecordCodecBuilder.create(builder -> {
		return builder.group(Codec.STRING.flatXmap(str -> str.length() > 32 ? DataResult.error("The title is longer than 32 characters.") : DataResult.success(str), str -> str.length() > 32 ? DataResult.error("The title is longer than 32 characters.") : DataResult.success(str)).fieldOf("title").forGetter(inst -> inst.title),
				Codec.STRING.fieldOf("author").forGetter(inst -> inst.author),
				Codec.intRange(0, 3).optionalFieldOf("generation", 0).forGetter(inst -> inst.generation),
				GeneralCodecs.TEXT_COMPONENT_CODEC.listOf().fieldOf("pages").forGetter(inst -> inst.pages))
				.apply(builder, BookInformation::new);
	});
	private final String title, author;
	private final int generation;
	private final List<ITextComponent> pages;
	
	/**
	 * A simple constructor, should be called
	 * through the codec. Left public for
	 * encode/decode.
	 * 
	 * @param title The book's title
	 * @param author The book's author
	 * @param generation The generation of the book
	 * @param pages The pages in the book
	 */
	public BookInformation(final String title, final String author, final int generation, final List<ITextComponent> pages) {
		this.title = title;
		this.author = author;
		this.generation = generation;
		this.pages = Util.make(new ArrayList<>(), list -> list.addAll(pages));
	}
	
	/**
	 * Converts the book information into
	 * its nbt form.
	 * 
	 * @return The nbt holding the book information
	 */
	public CompoundNBT toNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("title", this.title);
		nbt.putString("author", this.author);
		nbt.putInt("generation", this.generation);
		ListNBT pages = Util.make(new ListNBT(), list -> this.pages.forEach(page -> list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(page)))));
		nbt.put("pages", pages);
		return nbt;
	}
}
