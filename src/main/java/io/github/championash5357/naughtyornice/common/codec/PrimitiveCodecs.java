package io.github.championash5357.naughtyornice.common.codec;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;

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

	//TODO: Check if needed
	public static final PrimitiveCodec<CompoundNBT> COMPOUND_NBT = new PrimitiveCodec<CompoundNBT>() {

		private final Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

		@Override
		public <T> DataResult<CompoundNBT> read(DynamicOps<T> ops, T input) {
			if(ops instanceof JsonOps) {
				try {
					CompoundNBT nbt;
					if(input instanceof JsonElement) {
						nbt = ((JsonElement) input).isJsonObject() ? JsonToNBT.getTagFromJson(this.gson.toJson(input)) : JsonToNBT.getTagFromJson(JSONUtils.getString((JsonElement) input, "nbt"));
					} else if(input instanceof String) {
						nbt = JsonToNBT.getTagFromJson((String) input);
					} else return DataResult.error("The compound nbt is not in a valid form.");
					
					return DataResult.success(nbt).setLifecycle(Lifecycle.stable());
				} catch (CommandSyntaxException e) {
					throw new JsonSyntaxException("Invalid NBT Entry: " + e.toString());
				}
			} else return CompoundNBT.CODEC.decode(ops, input).map(pair -> pair.getFirst());
		}

		@Override
		public <T> T write(DynamicOps<T> ops, CompoundNBT value) {
			if(ops instanceof JsonOps) {
				return ops.createString(value.toString());
			} else return CompoundNBT.CODEC.encodeStart(ops, value).getOrThrow(false, str -> {});
		}
	};
}
