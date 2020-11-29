package io.github.championash5357.naughtyornice.common.present;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.championash5357.naughtyornice.api.present.Present;
import io.github.championash5357.naughtyornice.common.present.LootTablePresent.Wrapper;
import io.github.championash5357.naughtyornice.common.util.Helper;
import io.github.championash5357.naughtyornice.common.util.LootSpawnLocation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class LootTablePresent extends Present<Wrapper> {

	public LootTablePresent(Codec<Wrapper> codec) {
		super(codec);
	}

	@Override
	public boolean give(ServerPlayerEntity player, Wrapper config, BlockPos presentPos) {
		ServerWorld world = player.getServerWorld();
		Inventory inv = new Inventory(Helper.RANDOM.nextInt(config.size) + 1);
		world.getServer().getLootTableManager().getLootTableFromLocation(config.lootTable)
		.fillInventory(inv, (new LootContext.Builder(world))
				.withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(presentPos))
				.withSeededRandom(0L, Helper.RANDOM)
				.withLuck(player.getLuck())
				.withParameter(LootParameters.THIS_ENTITY, player)
				.build(LootParameterSets.CHEST));
		switch(config.lootSpawnLocation) {
		case GROUND:
			InventoryHelper.dropInventoryItems(player.world, presentPos, inv);
			return true;
		case INVENTORY:
			boolean drop = false;
			for(ItemStack stack : inv.func_233543_f_()) {
				if(drop) {
					InventoryHelper.spawnItemStack(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), stack);
				} else if(!player.addItemStackToInventory(stack)) drop = true;
			}
			return true;
		default:
			return false;
		}
	}
	
	public static class Wrapper {
		
		public static final Codec<Wrapper> CODEC = RecordCodecBuilder.create(builder -> {
			return builder.group(ResourceLocation.CODEC.fieldOf("name").forGetter(inst -> inst.lootTable),
					Codec.INT.optionalFieldOf("size", 1).forGetter(inst -> inst.size),
					Codec.STRING.xmap(LootSpawnLocation::getValue, LootSpawnLocation::getString).optionalFieldOf("loot_spawn_location", LootSpawnLocation.GROUND).forGetter(inst -> inst.lootSpawnLocation))
					.apply(builder, Wrapper::new);
		});
		
		private final ResourceLocation lootTable;
		private final int size;
		private final LootSpawnLocation lootSpawnLocation;
		
		public Wrapper(final ResourceLocation lootTable, final int size, final LootSpawnLocation lootSpawnLocation) {
			this.lootTable = lootTable;
			this.size = size;
			this.lootSpawnLocation = lootSpawnLocation;
		}
	}
}
