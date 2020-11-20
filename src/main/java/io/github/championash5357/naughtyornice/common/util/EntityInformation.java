package io.github.championash5357.naughtyornice.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.ashlib.util.CodecHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityInformation {

	public static final Codec<EntityInformation> CODEC = RecordCodecBuilder.create(builder -> {
		return builder.group(CodecHelper.createRegistryObjectCodec(ForgeRegistries.ENTITIES).fieldOf("type").forGetter(EntityInformation::getType),
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
