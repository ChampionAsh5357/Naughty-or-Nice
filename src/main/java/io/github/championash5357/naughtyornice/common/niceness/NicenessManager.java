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

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import commoble.databuddy.codec.MapCodecHelper;
import io.github.championash5357.ashlib.serialization.CodecHelper;
import io.github.championash5357.naughtyornice.api.util.DefinedJsonOps;
import io.github.championash5357.naughtyornice.api.util.FallbackCodec;
import io.github.championash5357.naughtyornice.common.util.StackInformation;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.*;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.*;
import net.minecraftforge.registries.ForgeRegistries;

public class NicenessManager extends JsonReloadListener {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Codec<Map<EntityType<?>, EntityNicenessEffect>> ENTITY_NICENESS_EFFECTS_CODEC = Codec.unboundedMap(CodecHelper.registryObject(ForgeRegistries.ENTITIES), EntityNicenessEffect.CODEC);
	private static final Codec<Map<String, Double>> GLOBAL_EFFECTS_CODEC = Codec.unboundedMap(Codec.STRING, Codec.DOUBLE);
	private final Codec<StackInformation> stackInformationCodec = FallbackCodec.create(StackInformation.CODEC,
			ResourceLocation.CODEC.comapFlatMap(location -> this.stackInformation.containsKey(location) ? DataResult.success(this.stackInformation.get(location)) : DataResult.error("No stack information exists at the following location: " + location), StackInformation::getId),
			CodecHelper.registryObject(ForgeRegistries.ITEMS).xmap(item -> new StackInformation(item, null), StackInformation::getItem));
	private final Codec<Map<StackInformation, Map<VillagerProfession, Double>>> villagerGiftsCodec = MapCodecHelper.makeEntryListCodec(this.stackInformationCodec, Codec.unboundedMap(CodecHelper.registryObject(ForgeRegistries.PROFESSIONS), Codec.DOUBLE));
	private final Map<EntityType<?>, EntityNicenessEffect> entityNicenessEffects = new HashMap<>();
	private final Map<String, Double> globalEffects = new HashMap<>();
	private final Map<ResourceLocation, StackInformation> stackInformation = new HashMap<>();
	private final Map<StackInformation, Map<VillagerProfession, Double>> villagerGifts = new HashMap<>();
	
	public NicenessManager() {
		super(GSON, "niceness");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		this.entityNicenessEffects.clear();
		this.globalEffects.clear();
		this.stackInformation.clear();
		this.villagerGifts.clear();
		List<JsonElement> tempVillagerGifts = new ArrayList<>();
		map.forEach((id, element) -> {
			if(id.getPath().equals("entity_niceness_effects")) this.parseEntityNicenessEffects(JSONUtils.getJsonObject(element, "entity_niceness_effects"));
			else if(id.getPath().equals("global_effects")) this.parseGlobalEffects(JSONUtils.getJsonObject(element, "global_effects"));
			else if(id.getPath().startsWith("stack_information/")) this.parseStackInformation(id, JSONUtils.getJsonObject(element, "stack_information"));
			else if(id.getPath().equals("villager_gifts")) tempVillagerGifts.add(element);
			else throw new JsonIOException("The following json file is incorrectly named or placed: " + id);
		});
		tempVillagerGifts.forEach(element -> this.parseVillagerGifts(JSONUtils.getJsonObject(element, "villager_gifts")));
	}
	
	private void parseEntityNicenessEffects(JsonObject obj) {
		if(JSONUtils.getBoolean(obj, "replace", false)) this.entityNicenessEffects.clear();
		this.entityNicenessEffects.putAll(ENTITY_NICENESS_EFFECTS_CODEC.parse(DefinedJsonOps.INSTANCE, JSONUtils.getJsonObject(obj, "entries")).resultOrPartial(Util.func_240982_a_("Error reading entity niceness effects after loading data packs: ", LOGGER::error)).orElse(new HashMap<>()));
	}
	
	private void parseGlobalEffects(JsonObject obj) {
		this.globalEffects.putAll(GLOBAL_EFFECTS_CODEC.parse(DefinedJsonOps.INSTANCE, obj).resultOrPartial(Util.func_240982_a_("Error reading global effects after loading data packs: ", LOGGER::error)).orElse(new HashMap<>()));
	}
	
	private void parseStackInformation(ResourceLocation id, JsonObject obj) {
		StackInformation.CODEC.parse(DefinedJsonOps.INSTANCE, obj).resultOrPartial(Util.func_240982_a_("Error reading stack information after loading data packs: ", LOGGER::error)).ifPresent(info -> this.stackInformation.put(new ResourceLocation(id.toString().replace("stack_information/", "")), info));
	}
	
	private void parseVillagerGifts(JsonObject obj) {
		if(JSONUtils.getBoolean(obj, "replace", false)) this.villagerGifts.clear();
		this.villagerGiftsCodec.parse(DefinedJsonOps.INSTANCE, JSONUtils.getJsonArray(obj, "entries"))
		.resultOrPartial(Util.func_240982_a_("Error reading villager gifts after loading data packs: ", LOGGER::error))
		.orElse(new HashMap<>())
		.entrySet().forEach(entry -> this.villagerGifts.computeIfAbsent(entry.getKey(), s -> new HashMap<>()).putAll(entry.getValue()));
	}
	
	public double getEntityHurt(EntityType<?> type, String... globalEffects) {
		return this.entityNicenessEffects.getOrDefault(type, EntityNicenessEffect.INSTANCE).getHurt() + this.getGlobalEffects(globalEffects);
	}
	
	public double getEntityDeath(EntityType<?> type, String... globalEffects) {
		return this.entityNicenessEffects.getOrDefault(type, EntityNicenessEffect.INSTANCE).getKill() + this.getGlobalEffects(globalEffects);
	}
	
	public double getEntityHeal(EntityType<?> type, String... globalEffects) {
		return this.entityNicenessEffects.getOrDefault(type, EntityNicenessEffect.INSTANCE).getHeal() + this.getGlobalEffects(globalEffects);
	}
	
	public double getEntitySpawn(EntityType<?> type, String... globalEffects) {
		return this.entityNicenessEffects.getOrDefault(type, EntityNicenessEffect.INSTANCE).getSpawn() + this.getGlobalEffects(globalEffects);
	}
	
	public double getVillagerGift(AbstractVillagerEntity villager, ItemStack stack) {
		@SuppressWarnings("unlikely-arg-type")
		Map<VillagerProfession, Double> professions = this.villagerGifts.entrySet().stream().filter(entry -> entry.getKey().equals(stack)).findFirst().map(entry -> entry.getValue()).orElse(null);
		if(professions == null) return 0.0;
		else if(villager instanceof VillagerEntity) return professions.getOrDefault(((VillagerEntity) villager).getVillagerData().getProfession(), professions.getOrDefault(VillagerProfession.NONE, 0.0));
		else return professions.getOrDefault(VillagerProfession.NONE, 0.0);
	}
	
	public double getGlobalEffects(String... globalEffects) {
		return Lists.newArrayList(globalEffects).stream().map(str -> this.globalEffects.getOrDefault(str, 0.0)).reduce(0.0, Double::sum);
	}
}
