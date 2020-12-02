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

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.IntStream;

/**
 * A simple helper that creates a bound, weighted element
 * of sorts to grab from a list when constructed.
 * 
 * @param <T> The specified object type
 */
public class WeightedBoundSet<T> {

	/**
	 * A static random instance.
	 */
	private static final Random RANDOM = new Random();
	/**
	 * A map from instance to its bound, weighted element.
	 */
	private final Map<T, WeightedBoundElement> map = new HashMap<>();
	
	/**
	 * Gets the associated map instance.
	 * 
	 * @return The map
	 */
	public Map<T, WeightedBoundElement> getMap() {
		return this.map;
	}
	
	/**
	 * Gets a random element based on the specified value
	 * between the bounds.
	 * 
	 * @param value The value to construct the element around
	 * @return The associated, valid element
	 */
	public Optional<T> getRandomElement(double value) {
		List<T> validElements = new ArrayList<>();
		//Map<Integer, T> validElements = new HashMap<>();
		for(Entry<T, WeightedBoundElement> entry : this.map.entrySet()) {
			if(entry.getValue().test(value)) {
				//int size = validElements.size();
				IntStream.range(0/*size*/, /*size + */entry.getValue().getWeight()).forEach(i -> validElements.add(entry.getKey())/*validElements.put(i, entry.getKey())*/);
			}
		}
		return validElements.size() > 0 ? Optional.of(validElements.get(RANDOM.nextInt(validElements.size()))) : Optional.empty();
	}
}
