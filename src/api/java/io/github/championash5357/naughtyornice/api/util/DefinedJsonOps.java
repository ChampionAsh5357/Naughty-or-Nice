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

import java.util.function.Function;

import com.google.gson.*;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

/**
 * An extension of JsonOps used to specify exact
 * primitive values for lists.
 */
public class DefinedJsonOps extends JsonOps {
	public static final DefinedJsonOps INSTANCE = new DefinedJsonOps(false);
	public static final DefinedJsonOps COMPRESSED = new DefinedJsonOps(true);
	
	protected DefinedJsonOps(boolean compressed) {
		super(compressed);
	}

	@Override
	public <U> U convertTo(DynamicOps<U> outOps, JsonElement input) {
		if(input instanceof JsonObject && input.getAsJsonObject().has("ops_data_type")) {
			JsonObject obj = input.getAsJsonObject();
			return createDefinedElement(outOps, obj.get("ops_data_type").getAsString()).apply(obj.get("value").getAsJsonPrimitive());
		} else return super.convertTo(outOps, input);
	}
	
	private static <T> Function<JsonPrimitive, T> createDefinedElement(final DynamicOps<T> ops, final String data_type) {
		switch(data_type) {
		case "byte":
			return primitive -> ops.createByte(primitive.getAsByte()); 
		case "short":
			return primitive -> ops.createShort(primitive.getAsShort()); 
		case "int":
			return primitive -> ops.createInt(primitive.getAsInt()); 
		case "long":
			return primitive -> ops.createLong(primitive.getAsLong()); 
		case "float":
			return primitive -> ops.createFloat(primitive.getAsFloat()); 
		case "double":
			return primitive -> ops.createDouble(primitive.getAsDouble()); 
		case "char":
			return primitive -> ops.createString("" + primitive.getAsCharacter()); 
		case "string":
			return primitive -> ops.createString(primitive.getAsString()); 
		default:
			throw new IllegalArgumentException("There is no supported primitive or string for the specified data type: " + data_type);
		}
	}
}
