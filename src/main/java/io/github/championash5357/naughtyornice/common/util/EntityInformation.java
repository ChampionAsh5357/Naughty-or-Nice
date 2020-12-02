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

package io.github.championash5357.naughtyornice.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.ashlib.serialization.CodecHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityInformation {

	public static final Codec<EntityInformation> CODEC = RecordCodecBuilder.create(builder -> {
		return builder.group(CodecHelper.registryObject(ForgeRegistries.ENTITIES).fieldOf("type").forGetter(EntityInformation::getType),
				EntityPos.CODEC.forGetter(EntityInformation::getPos),
				CompoundNBT.CODEC.optionalFieldOf("nbt", new CompoundNBT()).forGetter(EntityInformation::getNbt))
				.apply(builder, EntityInformation::new);
	});
	private final EntityType<?> type;
	private final EntityPos pos;
	private final CompoundNBT nbt;
	
	public EntityInformation(final EntityType<?> type, final EntityPos pos, final CompoundNBT nbt) {
		this.type = type;
		this.pos = pos;
		this.nbt = nbt;
	}
	
	public EntityType<?> getType() {
		return this.type;
	}
	
	public EntityPos getPos() {
		return this.pos;
	}
	
	public CompoundNBT getNbt() {
		return this.nbt;
	}
}
