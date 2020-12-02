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

import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.mutable.MutableObject;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;

import net.minecraft.util.NonNullList;

public class NonNullListCodec<A> implements Codec<NonNullList<A>> {

	private final Codec<A> elementCodec;
	
	public NonNullListCodec(final Codec<A> elementCodec) {
		this.elementCodec = elementCodec;
	}
	
	@Override
	public <T> DataResult<T> encode(NonNullList<A> input, DynamicOps<T> ops, T prefix) {
		final ListBuilder<T> builder = ops.listBuilder();

        for (final A a : input) {
            builder.add(elementCodec.encodeStart(ops, a));
        }

        return builder.build(prefix);
	}

	@Override
	public <T> DataResult<Pair<NonNullList<A>, T>> decode(DynamicOps<T> ops, T input) {
		return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(consumer -> {
			final NonNullList<A> read = NonNullList.create();
			final Stream.Builder<T> failed = Stream.builder();
            // TODO: AtomicReference.getPlain/setPlain in java9+
			final MutableObject<DataResult<Unit>> result = new MutableObject<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));
			
			consumer.accept(t -> {
				final DataResult<Pair<A, T>> element = this.elementCodec.decode(ops, t);
				element.error().ifPresent(e -> failed.add(t));
				result.setValue(result.getValue().apply2stable((r, v) -> {
					read.add(v.getFirst());
                    return r;
				}, element));
			});
			
			final T errors = ops.createList(failed.build());
			final Pair<NonNullList<A>, T> pair = Pair.of(read, errors);
			return result.getValue().map(unit -> pair).setPartial(pair);
		});
	}
	
	@Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NonNullListCodec<?> listCodec = (NonNullListCodec<?>) o;
        return Objects.equals(this.elementCodec, listCodec.elementCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.elementCodec);
    }

    @Override
    public String toString() {
        return "NonNullListCodec[" + elementCodec + ']';
    }
}
