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

package io.github.championash5357.naughtyornice.common.niceness;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class EntityNicenessEffect {

	private final double heal, hurt, kill, spawn;
	public static final Codec<EntityNicenessEffect> CODEC = RecordCodecBuilder.create(builder -> {
		return builder.group(Codec.DOUBLE.optionalFieldOf("heal", 0.0).forGetter(EntityNicenessEffect::getHeal),
				Codec.DOUBLE.optionalFieldOf("hurt", 0.0).forGetter(EntityNicenessEffect::getHurt),
				Codec.DOUBLE.optionalFieldOf("kill", 0.0).forGetter(EntityNicenessEffect::getKill),
				Codec.DOUBLE.optionalFieldOf("spawn", 0.0).forGetter(EntityNicenessEffect::getSpawn))
				.apply(builder, EntityNicenessEffect::new);
	});
	public static final EntityNicenessEffect INSTANCE = new EntityNicenessEffect(0.0, 0.0, 0.0, 0.0);
	
	public EntityNicenessEffect(final double heal, final double hurt, final double kill, final double spawn) {
		this.heal = heal;
		this.hurt = hurt;
		this.kill = kill;
		this.spawn = spawn;
	}
	
	public double getHeal() {
		return this.heal;
	}
	
	public double getHurt() {
		return this.hurt;
	}
	
	public double getKill() {
		return this.kill;
	}
	
	public double getSpawn() {
		return this.spawn;
	}
}
