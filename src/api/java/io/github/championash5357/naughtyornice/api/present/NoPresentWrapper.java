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

/**
 * A basic implementation of no configuration or wrapper
 * for a present if needed.
 */
public class NoPresentWrapper {
	/**
	 * The present wrapper instance.
	 */
	public static final NoPresentWrapper INSTANCE = new NoPresentWrapper();
	/**
	 * The codec of the empty wrapper.
	 */
	public static final Codec<NoPresentWrapper> CODEC = Codec.unit(() -> INSTANCE);
}
