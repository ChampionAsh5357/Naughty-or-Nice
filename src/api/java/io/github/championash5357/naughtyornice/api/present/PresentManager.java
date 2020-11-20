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

package io.github.championash5357.naughtyornice.api.present;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import commoble.databuddy.codec.MapCodecHelper;
import io.github.championash5357.naughtyornice.api.util.*;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.*;

/**
 * Grabs the present information. All presents
 * are handled through JSON only except logic.
 */
public class PresentManager extends JsonReloadListener {

	private static final PresentManager INSTANCE = new PresentManager();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger LOGGER = LogManager.getLogger();
	private final BiMap<ResourceLocation, WrappedPresent<?, ?>> wrappedPresents = HashBiMap.create();
	private final WeightedBoundSet<WrappedPresent<?, ?>> gifts = new WeightedBoundSet<>();
	private final Codec<WrappedPresent<?, ?>> wrappedPresentCodec = FallbackCodec.create(WrappedPresent.CODEC, ResourceLocation.CODEC.xmap(loc -> this.wrappedPresents.get(loc), wrap -> this.wrappedPresents.inverse().get(wrap)));
	private final Codec<Map<WrappedPresent<?, ?>, WeightedElement>> giftsCodec = MapCodecHelper.makeEntryListCodec(this.wrappedPresentCodec, WeightedElement.CODEC);
	
	public PresentManager() {
		super(GSON, "presents");
	}
	
	public static final PresentManager getInstance() {
		return INSTANCE;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		this.wrappedPresents.clear();
		this.gifts.getMap().clear();
		List<JsonElement> gifts = new ArrayList<>();
		map.forEach((id, element) -> {
			if(id.getPath().startsWith("wrapped/")) this.parseWrappedPresent(id, JSONUtils.getJsonObject(element, "wrapped_present"));
			else if(id.getPath().equals("gifts")) gifts.add(element);
			else throw new JsonIOException("The following json file is incorrectly named or placed: " + id);
		});
		gifts.forEach(element -> this.parseGifts(JSONUtils.getJsonObject(element, "gifts")));
	}
	
	private void parseWrappedPresent(ResourceLocation id, JsonObject obj) {
		WrappedPresent.CODEC.parse(JsonOps.INSTANCE, obj).resultOrPartial(Util.func_240982_a_("Error reading wrapped present after loading data packs: ", LOGGER::error)).ifPresent(info -> this.wrappedPresents.put(new ResourceLocation(id.toString().replace("wrapped/", "")), info));
	}
	
	private void parseGifts(JsonObject obj) {
		if(JSONUtils.getBoolean(obj, "replace", false)) this.gifts.getMap().clear();
		this.gifts.getMap().putAll(this.giftsCodec.parse(JsonOps.INSTANCE, JSONUtils.getJsonObject(obj, "entries")).resultOrPartial(Util.func_240982_a_("Error reading villager gifts after loading data packs: ", LOGGER::error)).orElse(new HashMap<>()));
	}
	
	/**
	 * Grabs a wrapped present codec that constructs from the resource
	 * location.
	 * 
	 * @return The wrapped present codec
	 */
	public Codec<WrappedPresent<?, ?>> getWrappedPresentCodec() {
		return this.wrappedPresentCodec;
	}
	
	/**
	 * Gets a random wrapped present based on the user's niceness level.
	 * 
	 * @param niceness The user's niceness level
	 * @return An {@code Optional} containing the wrapped present
	 */
	public Optional<WrappedPresent<?, ?>> getWrappedPresent(double niceness) {
		return this.gifts.getRandomElement(niceness);
	}
	
	/**
	 * Gets a wrapped present based on its name.
	 * 
	 * @param location The name
	 * @return A wrapped present
	 */
	public WrappedPresent<?, ?> getWrappedPresent(ResourceLocation location) {
		return this.wrappedPresents.get(location);
	}
	
	/**
	 * Grabs the name of a present from the present itself.
	 * 
	 * @param present The present
	 * @return The present's name
	 */
	public ResourceLocation reversePresent(WrappedPresent<?, ?> present) {
		return this.wrappedPresents.inverse().get(present);
	}
}
