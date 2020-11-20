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

import java.util.Arrays;
import java.util.Objects;

/**
 * A helper that does a bit of simple manipulation to treat one value
 * as multiple numbers.
 */
public class ManyToOneInteger {

	/**
	 * The associated keys.
	 */
	private int[] keys;
	/**
	 * The actual value
	 */
	private int value;
	
	/**
	 * A simple constructor.
	 * 
	 * @param value The actual value of this integer
	 * @param keys The numbers that can represent this integer
	 */
	public ManyToOneInteger(final int value, final int... keys) {
		this.keys = keys;
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(this.value);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Integer) return Arrays.stream(this.keys).anyMatch(i -> ((Integer) o).intValue() == i) ? true : false;
		return super.equals(o);
	}
}
