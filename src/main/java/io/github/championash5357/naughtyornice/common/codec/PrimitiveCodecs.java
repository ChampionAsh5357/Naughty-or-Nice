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

package io.github.championash5357.naughtyornice.common.codec;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class PrimitiveCodecs {

	public static final PrimitiveCodec<DoubleStream> DOUBLE_STREAM = new PrimitiveCodec<DoubleStream>() {

		@Override
		public <T> DataResult<DoubleStream> read(DynamicOps<T> ops, T input) {
			return ops.getStream(input).flatMap(stream -> {
				final List<T> list = stream.collect(Collectors.toList());
				if(list.stream().allMatch(element -> ops.getNumberValue(element).result().isPresent())) {
					return DataResult.success(list.stream().mapToDouble(element -> ops.getNumberValue(element).result().get().doubleValue()));
				}
				return DataResult.error("Some elements are not doubles: " + input);
			});
		}

		@Override
		public <T> T write(DynamicOps<T> ops, DoubleStream value) {
			return ops.createList(value.mapToObj(ops::createDouble));
		}

		@Override
		public String toString() {
			return "DoubleStream";
		}
	};
}
