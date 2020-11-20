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

package io.github.championash5357.naughtyornice.common.init;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;

import commoble.databuddy.codec.MapCodecHelper;
import io.github.championash5357.ashlib.registry.DeferredRegistryHelper;
import io.github.championash5357.naughtyornice.api.present.*;
import io.github.championash5357.naughtyornice.common.NaughtyOrNice;
import io.github.championash5357.naughtyornice.common.block.PresentBlock;
import io.github.championash5357.naughtyornice.common.present.*;
import io.github.championash5357.naughtyornice.common.tileentity.PresentTileEntity;
import io.github.championash5357.naughtyornice.common.util.EntityInformation;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryBuilder;

public class GeneralRegistrar {

	public static final DeferredRegistryHelper<Block> BLOCKS = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry(ForgeRegistries.BLOCKS);
	public static final DeferredRegistryHelper<Item> ITEMS = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry(ForgeRegistries.ITEMS);
	public static final DeferredRegistryHelper<TileEntityType<?>> TILE_ENTITY_TYPES = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry(ForgeRegistries.TILE_ENTITIES);	
	@SuppressWarnings("unchecked")
	public static final DeferredRegistryHelper<Present<?>> PRESENTS = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry("present", Present.class, () -> new RegistryBuilder<>());
	
	public static final RegistryObject<PresentBlock> PRESENT = register("present", () -> new PresentBlock(AbstractBlock.Properties.create(Material.WOOL)), (sup) -> () -> new BlockItem(sup.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
	public static final RegistryObject<TileEntityType<PresentTileEntity>> PRESENT_TYPE = TILE_ENTITY_TYPES.register("present", () -> TileEntityType.Builder.create(PresentTileEntity::new, PRESENT.get()).build(null));

	public static final RegistryObject<MultiChancePresent> MULTI_CHANCE = PRESENTS.register("multi_chance", () -> new MultiChancePresent(MapCodecHelper.makeEntryListCodec(PresentManager.getInstance().getWrappedPresentCodec(), Codec.DOUBLE.orElse(1.0))));
	public static final RegistryObject<ItemStackPresent> ITEM_STACK = PRESENTS.register("item_stack", () -> new ItemStackPresent(ItemStackPresent.Wrapper.CODEC));
	public static final RegistryObject<EntityPresent> ENTITY = PRESENTS.register("entity", () -> new EntityPresent(EntityInformation.CODEC.listOf()));
	public static final RegistryObject<JigsawPresent> JIGSAW = PRESENTS.register("jigsaw", () -> new JigsawPresent(JigsawPresent.Wrapper.CODEC));
	public static final RegistryObject<StructurePresent> STRUCTURE = PRESENTS.register("structure", () -> new StructurePresent(StructurePresent.Wrapper.CODEC));
	
	private static <V extends Block> RegistryObject<V> register(String name, Supplier<V> blockSupplier, @Nullable Function<Supplier<V>, Supplier<? extends Item>> itemFunction) {
		RegistryObject<V> obj = BLOCKS.register(name, blockSupplier);
		if(itemFunction != null) ITEMS.register(name, itemFunction.apply(obj));
		return obj;
	}
}
