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
import io.github.championash5357.ashlib.registry.*;
import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.api.present.PresentManager;
import io.github.championash5357.naughtyornice.common.NaughtyOrNice;
import io.github.championash5357.naughtyornice.common.block.PresentBlock;
import io.github.championash5357.naughtyornice.common.present.*;
import io.github.championash5357.naughtyornice.common.tileentity.PresentTileEntity;
import io.github.championash5357.naughtyornice.common.util.EntityInformation;
import io.github.championash5357.naughtyornice.common.world.gen.feature.template.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.IPosRuleTests;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryBuilder;

public class GeneralRegistrar {

	public static final DeferredRegistryHelper<Block> BLOCKS = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry(ForgeRegistries.BLOCKS);
	public static final DeferredRegistryHelper<Item> ITEMS = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry(ForgeRegistries.ITEMS);
	public static final DeferredRegistryHelper<TileEntityType<?>> TILE_ENTITY_TYPES = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry(ForgeRegistries.TILE_ENTITIES);	
	public static final DeferredRegistryHelper<SoundEvent> SOUND_EVENTS = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry(ForgeRegistries.SOUND_EVENTS);	
	@SuppressWarnings("unchecked")
	public static final DeferredRegistryHelper<Present<?>> PRESENTS = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry("present", Present.class, () -> new RegistryBuilder<>());
	public static final VanillaRegistryHelper<IStructureProcessorType<?>> STRUCTURE_PROCESSORS = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry(Registry.STRUCTURE_PROCESSOR, false);
	public static final VanillaRegistryHelper<IPosRuleTests<?>> POS_RULE_TESTS = NaughtyOrNice.getInstance().getRegistryHelper().createRegistry(Registry.POS_RULE_TEST, false);
	
	public static final RegistryObject<PresentBlock> PRESENT = register("present", () -> new PresentBlock(AbstractBlock.Properties.create(Material.WOOL)), (sup) -> () -> new BlockItem(sup.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
	public static final RegistryObject<TileEntityType<PresentTileEntity>> PRESENT_TYPE = TILE_ENTITY_TYPES.register("present", () -> TileEntityType.Builder.create(PresentTileEntity::new, PRESENT.get()).build(null));
	public static final RegistryObject<SoundEvent> BLOCK_PRESENT_OPEN = register("block.present.open");

	public static final VanillaRegistryObject<IStructureProcessorType<PresentProcessor>> PRESENT_PROCESSOR = STRUCTURE_PROCESSORS.register("present", () -> () -> PresentProcessor.CODEC);
	public static final VanillaRegistryObject<IStructureProcessorType<SignProcessor>> SIGN_PROCESSOR = STRUCTURE_PROCESSORS.register("sign", () -> () -> SignProcessor.CODEC);
	public static final VanillaRegistryObject<IStructureProcessorType<RandomProcessor>> RANDOM_PROCESSOR = STRUCTURE_PROCESSORS.register("random", () -> () -> RandomProcessor.CODEC);
	public static final VanillaRegistryObject<IPosRuleTests<AtYZeroTest>> AT_Y_ZERO_POS_TEST = POS_RULE_TESTS.register("at_y_zero", () -> () -> AtYZeroTest.CODEC);
	
	public static final RegistryObject<MultiChancePresent> MULTI_CHANCE = PRESENTS.register("multi_chance", () -> new MultiChancePresent(MapCodecHelper.makeEntryListCodec(PresentManager.getInstance().getWrappedPresentCodec(), Codec.doubleRange(0.0, 1.0).orElse(1.0))));
	public static final RegistryObject<LootTablePresent> LOOT_TABLE = PRESENTS.register("loot_table", () -> new LootTablePresent(LootTablePresent.Wrapper.CODEC));
	public static final RegistryObject<EntityPresent> ENTITY = PRESENTS.register("entity", () -> new EntityPresent(EntityInformation.CODEC.listOf()));
	public static final RegistryObject<JigsawPresent> JIGSAW = PRESENTS.register("jigsaw", () -> new JigsawPresent(JigsawPresent.Wrapper.CODEC));
	public static final RegistryObject<StructurePresent> STRUCTURE = PRESENTS.register("structure", () -> new StructurePresent(StructurePresent.Wrapper.CODEC));
	public static final RegistryObject<PlayerNbtPresent> PLAYER_NBT = PRESENTS.register("player_nbt", () -> new PlayerNbtPresent(CompoundNBT.CODEC));
	public static final RegistryObject<CommandPresent> COMMAND = PRESENTS.register("command", () -> new CommandPresent(Codec.STRING));
	
	private static <V extends Block> RegistryObject<V> register(String name, Supplier<V> blockSupplier, @Nullable Function<Supplier<V>, Supplier<? extends Item>> itemFunction) {
		RegistryObject<V> obj = BLOCKS.register(name, blockSupplier);
		if(itemFunction != null) ITEMS.register(name, itemFunction.apply(obj));
		return obj;
	}
	
	private static RegistryObject<SoundEvent> register(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(NaughtyOrNice.ID, name)));
	}
}
