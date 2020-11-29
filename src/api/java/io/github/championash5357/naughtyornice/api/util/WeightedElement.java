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

import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * A simple weighted element implementation. Holds the 
 * weight of an item and the associated bounds where the
 * weight is applicable.
 */
public class WeightedElement implements Predicate<Double> {

	/**
	 * The class codec.
	 */
	public static final Codec<WeightedElement> CODEC = RecordCodecBuilder.create(builder -> {
		return builder.group(Codec.INT.fieldOf("weight").forGetter(WeightedElement::getWeight),
				Codec.DOUBLE.optionalFieldOf("min", Double.NEGATIVE_INFINITY).forGetter(element -> element.min),
				Codec.DOUBLE.optionalFieldOf("max", Double.POSITIVE_INFINITY).forGetter(element -> element.max))
				.apply(builder, WeightedElement::new);
	});
	/**
	 * The weight value.
	 */
	private final int weight;
	/**
	 * The associated bounds.
	 */
	private final double min, max;
	
	/**
	 * A simple constructor.
	 * 
	 * @param weight The weight
	 * @param min The minimum bound
	 * @param max The maximum bound
	 */
	public WeightedElement(final int weight, final double min, final double max) {
		this.weight = weight;
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Gets the weight of the element.
	 * 
	 * @return The element weight
	 */
	public int getWeight() {
		return this.weight;
	}

	/**
	 * Tests whether or not the element is between the bounds.
	 */
	@Override
	public boolean test(Double t) {
		return t >= this.min && t <= this.max;
	}
}
