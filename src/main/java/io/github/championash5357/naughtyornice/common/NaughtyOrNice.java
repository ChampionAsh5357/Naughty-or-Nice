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

package io.github.championash5357.naughtyornice.common;

import java.util.stream.IntStream;

import io.github.championash5357.ashlib.capability.CapabilityProviderBuilder;
import io.github.championash5357.ashlib.data.LocalizationHelper;
import io.github.championash5357.ashlib.registry.RegistryHelper;
import io.github.championash5357.naughtyornice.api.capability.*;
import io.github.championash5357.naughtyornice.api.event.PlayerHealLivingEvent;
import io.github.championash5357.naughtyornice.api.present.PresentManager;
import io.github.championash5357.naughtyornice.client.ClientReference;
import io.github.championash5357.naughtyornice.common.init.CapabilityRegistrar;
import io.github.championash5357.naughtyornice.common.init.GeneralRegistrar;
import io.github.championash5357.naughtyornice.common.niceness.NicenessManager;
import io.github.championash5357.naughtyornice.common.tileentity.PresentTileEntity;
import io.github.championash5357.naughtyornice.common.util.Helper;
import io.github.championash5357.naughtyornice.common.util.LocalizationStrings;
import io.github.championash5357.naughtyornice.data.client.BlockStates;
import io.github.championash5357.naughtyornice.data.server.LootTables;
import io.github.championash5357.naughtyornice.server.dedicated.DedicatedServerReference;
import net.minecraft.block.Block;
import net.minecraft.block.CropsBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBiomeReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NaughtyOrNice.ID)
public class NaughtyOrNice {

	public static final String ID = "naughtyornice";
	public static final ISidedReference SIDED_SYSTEM = DistExecutor.safeRunForDist(() -> ClientReference::new, () -> DedicatedServerReference::new);
	private static NaughtyOrNice instance;
	private final NicenessManager nicenessManager;
	private final RegistryHelper registryHelper;

	public NaughtyOrNice() {
		final IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus(),
				forge = MinecraftForge.EVENT_BUS;
		
		instance = this;
		this.nicenessManager = new NicenessManager();
		this.registryHelper = new RegistryHelper(ID, mod);
		
		GeneralRegistrar.class.getClass();

		new LocalizationHelper(ID, mod, false).createLocalization("en_us")
		.addBlock(GeneralRegistrar.PRESENT, "Present")
		.add(LocalizationStrings.SUBTITLE_BLOCK_PRESENT_OPEN, "Present Opening")
		.add(LocalizationStrings.NICENESS_CHECK, "%1$s has a niceness of %2$s.")
		.add(LocalizationStrings.NICENESS_CHECK_ERROR, "You cannot check the niceness of a player for another %1$s ticks.")
		.add(LocalizationStrings.PRESENT_MESSAGE_SINGLE_TNT, "Surprise!")
		.add(LocalizationStrings.PRESENT_MESSAGE_CREEPER_PARTY, "Creeper Party!")
		.end();

		mod.addListener(this::common);
		mod.addListener(this::gatherData);
		forge.addListener(this::tickPlayer);
		forge.addListener(this::addListeners);
		forge.addListener(this::livingHeal);
		forge.addListener(this::slept);
		forge.addGenericListener(Entity.class, this::attachPlayerCaps);
		forge.addListener(EventPriority.LOWEST, this::playerInteraction);
		forge.addListener(EventPriority.LOWEST, this::livingHurt);
		forge.addListener(EventPriority.LOWEST, this::livingDeath);
		forge.addListener(EventPriority.LOWEST, this::breeding);
		forge.addListener(EventPriority.LOWEST, this::blockBreak);
		SIDED_SYSTEM.setup(mod, forge);
	}

	public static final NaughtyOrNice getInstance() {
		return instance;
	}

	public final NicenessManager getNicenessManager() {
		return this.nicenessManager;
	}
	
	public final RegistryHelper getRegistryHelper() {
		return this.registryHelper;
	}
	
	private void slept(final SleepFinishedTimeEvent event) {
		if(event.getWorld().isRemote()) return;
		IWorld world = event.getWorld();
		world.getPlayers().forEach(player -> {
			final ChunkPos playerChunk = new ChunkPos(player.getPosition());
			IntStream.range(0, 9).filter(chunkOffset -> Helper.RANDOM.nextDouble() < 0.5).forEach(chunkOffset -> {
				final BlockPos chunkPos = new ChunkPos(playerChunk.x + (chunkOffset % 3) - 1, playerChunk.z + ((chunkOffset / 3) % 3) - 1).asBlockPos();
				IntStream.range(0, Helper.RANDOM.nextInt(64) + 1).forEach(rand -> this.tryAndSpawnPresent(world, world.getHeight(Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(Helper.RANDOM.nextInt(16) + chunkPos.getX(), chunkPos.getY(), Helper.RANDOM.nextInt(16) + chunkPos.getZ()))));
			});
		});
	}
	
	private void tryAndSpawnPresent(IBiomeReader reader, BlockPos pos) {
		if(IntStream.range(0, 27).anyMatch(i -> reader.getBlockState(pos.add((i % 3) - 1, ((i / 3) % 3) - 1, ((i / 9) % 3) - 1)).isIn(BlockTags.LOGS))) {
			reader.setBlockState(pos, GeneralRegistrar.PRESENT.get().getDefaultState(), BlockFlags.DEFAULT);
			TileEntity te = reader.getTileEntity(pos);
			if(te instanceof PresentTileEntity) ((PresentTileEntity) te).setNiceness(Helper.RANDOM.nextInt(201) - 100);
		}
	}
	
	private void livingHurt(final LivingDamageEvent event) {
		if(event.isCanceled() || event.getEntity().world.isRemote) return;
		if(event.getSource().getTrueSource() instanceof PlayerEntity) {
			event.getSource().getTrueSource().getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> {
				LivingEntity entity = event.getEntityLiving();
				if(entity.isChild()) inst.changeNiceness(this.getNicenessManager().getEntityHurt(entity.getType(), "child_hurt"));
				else inst.changeNiceness(this.getNicenessManager().getEntityHurt(entity.getType()));
			});
		}
	}

	private void livingDeath(final LivingDeathEvent event) {
		if(event.isCanceled() || event.getEntity().world.isRemote) return;
		if(event.getSource().getTrueSource() instanceof PlayerEntity) {
			event.getSource().getTrueSource().getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> {
				LivingEntity entity = event.getEntityLiving();
				if(entity.isChild()) inst.changeNiceness(this.getNicenessManager().getEntityDeath(entity.getType(), "child_kill"));
				else inst.changeNiceness(this.getNicenessManager().getEntityDeath(entity.getType()));
			});
		}
	}

	private void breeding(final BabyEntitySpawnEvent event) {
		if(event.isCanceled() || event.getCausedByPlayer() == null || event.getParentA().world.isRemote) return;
		if(event.getCausedByPlayer() instanceof PlayerEntity) {
			event.getCausedByPlayer().getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> {
				inst.changeNiceness(this.getNicenessManager().getGlobalEffects("breeding"));
			});
		}
	}

	private void livingHeal(final PlayerHealLivingEvent event) {
		if(event.getPlayer().world.isRemote) return;
		event.getPlayer().getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> {
			LivingEntity entity = event.getHealedEntity();
			if(entity.isChild()) inst.changeNiceness(this.getNicenessManager().getEntityDeath(entity.getType(), "child_heal"));
			else inst.changeNiceness(this.getNicenessManager().getEntityDeath(entity.getType()));
		});
	}
	
	private void attachPlayerCaps(final AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof PlayerEntity)
			event.addCapability(CapabilityRegistrar.NICENESS_CAPABILITY_NAME, CapabilityProviderBuilder.create()
					.attachCapability(CapabilityInstances.NICENESS_CAPABILITY)
					.addDefaultInstance(new Niceness((PlayerEntity) event.getObject()))
					.finish()
					.buildSerializable(event::addListener));
	}

	private void common(final FMLCommonSetupEvent event) {
		CapabilityRegistrar.register();
	}

	private void tickPlayer(final PlayerTickEvent event) {
		if(event.side == LogicalSide.CLIENT || event.phase == Phase.START || !event.player.isAlive()) return;
		event.player.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(INiceness::tick);
	}

	private void playerInteraction(final PlayerInteractEvent.EntityInteract event) {
		if(event.isCanceled() || event.getPlayer().world.isRemote) return;
		PlayerEntity player = event.getPlayer();
		if(event.getTarget() instanceof PlayerEntity) {
			player.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> inst.getNiceness((PlayerEntity) event.getTarget()));
		} else if(event.getTarget() instanceof AbstractVillagerEntity) {
			player.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> {
				ItemStack stack = player.getHeldItem(event.getHand());
				Entity entity = event.getTarget();
				double amt = this.getNicenessManager().getVillagerGift((AbstractVillagerEntity) entity, stack);
				if(amt != 0) {
					stack.shrink(1);
					event.setCanceled(true);
					event.setCancellationResult(ActionResultType.func_233537_a_(player.world.isRemote));
					((ServerWorld) player.world).spawnParticle(amt < 0 ? ParticleTypes.ANGRY_VILLAGER : ParticleTypes.HAPPY_VILLAGER, entity.getPosX(), entity.getPosYRandom() + 1.0, entity.getPosZ(), 5, 1.0, 1.0, 1.0, 0.02);
					inst.changeNiceness(amt);
				}
			});
		}
	}
	
	private void blockBreak(final BlockEvent.BreakEvent event) {
		if(event.isCanceled() || event.getState() == null || event.getPlayer() == null || event.getPlayer().world.isRemote) return;
		Block block = event.getState().getBlock();
		if(block instanceof CropsBlock && ((CropsBlock) block).isMaxAge(event.getState())) {
			event.getPlayer().getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> {
				inst.changeNiceness(this.getNicenessManager().getGlobalEffects("farm"));
			});
		}
	}

	private void addListeners(final AddReloadListenerEvent event) {
		event.addListener(this.nicenessManager);
		event.addListener(PresentManager.getInstance());
	}
	
	private void gatherData(final GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		if(event.includeClient()) {
			gen.addProvider(new BlockStates(gen, ID, event.getExistingFileHelper()));
		}
		if(event.includeServer()) {
			gen.addProvider(new LootTables(gen));
		}
	}
}
