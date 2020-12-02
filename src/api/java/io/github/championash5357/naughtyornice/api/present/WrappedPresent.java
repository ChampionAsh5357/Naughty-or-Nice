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

package io.github.championash5357.naughtyornice.api.present;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import io.github.championash5357.ashlib.serialization.CodecHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * An implementation similar to that of a {@code ConfiguredFeature} except
 * with the user and the present position passed in instead.
 * 
 * @param <T> A configuration relating to the present (also known as the wrapper)
 * @param <P> The actual present
 */
public class WrappedPresent<T, P extends Present<T>> {

	/**
	 * The associated wrapped present codec.
	 */
	public static final Codec<WrappedPresent<?, ?>> CODEC = CodecHelper.registryObject(Present.REGISTRY.get())
			.dispatch(wrapped -> wrapped.present, Present::getCodec);
	/**
	 * The present instance.
	 */
	private final P present;
	/**
	 * The present wrapper.
	 */
	private final T config;
	
	/**
	 * A simple constructor. Should be referenced
	 * via {@link Present#wrap(Object)}.
	 * 
	 * @param present The present instance
	 * @param config The present wrapper
	 */
	public WrappedPresent(P present, T config) {
		this.present = present;
		this.config = config;
	}
	
	/**
	 * Gets the present instance.
	 * 
	 * @return The present
	 */
	public P getPresent() {
		return present;
	}
	
	/**
	 * Gets the wrapper of the present.
	 * 
	 * @return The present wrapper.
	 */
	public T getConfig() {
		return config;
	}
	
	/**
	 * Unwraps a present by gifting it to the associated player.
	 * 
	 * @param player The associated player
	 * @param presentPos The present position
	 * @return If the present was unwrapped successfully; if not, an error will be thrown
	 */
	public DataResult<Present<T>> give(ServerPlayerEntity player, BlockPos presentPos) {
		try {
			return this.present.give(player, this.config, presentPos);
		} catch (Exception e) {
			return DataResult.error(e.getMessage());
		}
	}
}
