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
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;

/**
 * A codec that can be decoded by many different
 * codecs. The first codec instance will always
 * be the encoder.
 * 
 * @param <E> The object type of the codec
 */
public class FallbackCodec<E> implements Codec<E> {

	/**
	 * A list of possible codecs.
	 */
	private final List<Codec<E>> codecs;
	
	/**
	 * A simple constructor. Should reference via
	 * {@link FallbackCodec#create(Codec, Codec...)}.
	 * 
	 * @param codecs A list of codecs
	 */
	public FallbackCodec(List<Codec<E>> codecs) {
		if(codecs.size() == 0) throw new IllegalArgumentException("You cannot create a codec with no codecs!");
		this.codecs = codecs;
	}
	
	/**
	 * Constructs a codec that can be decoded using
	 * a variety of codecs.
	 * 
	 * @param <E> The object type of the codec
	 * @param codec The first decoder and only encoder codec
	 * @param codecs The other decoder codecs
	 * @return A new {@link FallbackCodec}
	 */
	@SafeVarargs
	public static <E> FallbackCodec<E> create(Codec<E> codec, Codec<E>... codecs) {
		List<Codec<E>> list = new ArrayList<>();
		list.add(codec);
		list.addAll(Lists.newArrayList(codecs));
		return new FallbackCodec<E>(list);
	}
	
	@Override
	public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
		return this.codecs.get(0).encode(input, ops, prefix);
	}

	@Override
	public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
		final Stream.Builder<Pair<E,T>> failed = Stream.builder();
		for(Codec<E> codec : this.codecs) {
			DataResult<Pair<E, T>> result = codec.decode(ops, input);
			if(result.error().isPresent()) result.map(pair -> {
				failed.add(pair);
				return pair;
			});
			else return result;
		}
		
		final Stream<Pair<E, T>> pairs = failed.build();
		final Pair<E, T> error = Pair.of(pairs.findFirst().get().getFirst(), ops.createList(pairs.map(pair -> pair.getSecond())));
		return DataResult.error("None of the codecs could deserialize the input: " + input, error, Lifecycle.stable());
	}
	
	@Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(this.codecs, ((FallbackCodec<?>) o).codecs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.codecs);
    }

    @Override
    public String toString() {
        return "FallbackCodec[" + this.codecs + ']';
    }
	
}
