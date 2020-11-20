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
