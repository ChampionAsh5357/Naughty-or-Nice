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

package io.github.championash5357.naughtyornice.common.present;

import java.util.*;

import com.mojang.serialization.*;

import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.api.present.WrappedPresent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class MultiChancePresent extends Present<Map<WrappedPresent<?, ?>, Double>> {

	public MultiChancePresent(Codec<Map<WrappedPresent<?, ?>, Double>> codec) {
		super(codec);
	}

	@Override
	public DataResult<Present<Map<WrappedPresent<?, ?>, Double>>> give(ServerPlayerEntity player, Map<WrappedPresent<?, ?>, Double> config, BlockPos presentPos) {
		if(config.isEmpty()) return DataResult.error("There are no presents available.", this, Lifecycle.stable());
		List<String> errors = new ArrayList<>();
		config.entrySet().stream().filter(entry -> Math.random() < entry.getValue()).forEach(entry -> {
			entry.getKey().give(player, presentPos).error().ifPresent(partial -> errors.add(partial.message()));
		});
		Optional<String> error = errors.stream().reduce((current, next) -> current + "\n" + next);
		return error.isPresent() ? DataResult.error(error.get(), this, Lifecycle.stable()) : DataResult.success(this, Lifecycle.stable());
	}
}
