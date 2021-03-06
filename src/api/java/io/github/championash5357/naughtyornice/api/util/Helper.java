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

import java.util.Random;
import java.util.function.Supplier;

/**
 * A utility class that simply holds some helper references.
 */
public class Helper {

	/**
	 * A random instance.
	 */
	public static final Random RANDOM = new Random();
	
	/**
	 * Creates a supplier function for lambda usage.
	 * 
	 * @param <T> The object type
	 * @param t The object instance
	 * @return A supplier of the object instance
	 */
	public static <T> Supplier<T> supplierFunction(T t) {
		return () -> t;
	}
}
