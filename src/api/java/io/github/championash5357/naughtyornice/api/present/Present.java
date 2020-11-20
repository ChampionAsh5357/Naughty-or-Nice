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

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * An implementation similar to that of a {@code Feature} except
 * with the user and the present position passed in instead.
 * 
 * @param <T> A configuration relating to the present (also known as the wrapper)
 */
public abstract class Present<T> extends ForgeRegistryEntry<Present<?>> {
	
	/**
	 * The present registry.
	 */
	@SuppressWarnings("unchecked")
	public static final Lazy<IForgeRegistry<Present<?>>> REGISTRY = () -> GameRegistry.findRegistry(Present.class);
	/**
	 * The associated wrapped present codec.
	 */
	private final Codec<WrappedPresent<T, Present<T>>> codec;
	
	/**
	 * A simple constructor.
	 * 
	 * @param codec The configuration codec
	 */
	public Present(Codec<T> codec) {
		this.codec = codec.fieldOf("config").xmap(config -> new WrappedPresent<>(this, config), wrapped -> wrapped.getConfig()).codec();
	}
	
	/**
	 * Gets the codec of the associated wrapped present.
	 * 
	 * @return The associated codec
	 */
	public Codec<WrappedPresent<T, Present<T>>> getCodec() {
		return this.codec;
	}
	
	/**
	 * Wraps a present with its configuration.
	 * 
	 * @param config The wrapper
	 * @return A wrapped present
	 */
	public WrappedPresent<T, ?> wrap(T config) {
		return new WrappedPresent<>(this, config);
	}
	
	/**
	 * Unwraps a present by gifting it to the associated player.
	 * 
	 * @param player The associated player
	 * @param config The present wrapper
	 * @param presentPos The present position
	 * @return If the present was unwrapped successfully; if not, an error will be thrown
	 */
	public abstract boolean give(ServerPlayerEntity player, T config, BlockPos presentPos);
}
