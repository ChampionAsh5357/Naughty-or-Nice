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

import javax.annotation.Nullable;

import com.mojang.serialization.*;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

/**
 * A class that holds general codecs not created
 * in the main library.
 */
public class GeneralCodecs {

	/**
	 * A codec for {@link ITextComponent}.
	 */
	public static final Codec<ITextComponent> TEXT_COMPONENT_CODEC = Codec.PASSTHROUGH.comapFlatMap((dynamic) -> {
		@Nullable IFormattableTextComponent component;
		try {
			component = ITextComponent.Serializer.getComponentFromJson(dynamic.convert(JsonOps.INSTANCE).getValue());
		} catch (Exception e) {
			return DataResult.error("The text component deserialized with an error: " + e.getMessage());
		}
		return component != null ? DataResult.success(component) : DataResult.error("Not a valid formattable text component, returned null.");
	}, text -> new Dynamic<>(JsonOps.INSTANCE, ITextComponent.Serializer.toJsonTree(text)));
}
