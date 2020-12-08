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

package io.github.championash5357.naughtyornice.api.capability;

import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.championash5357.naughtyornice.api.present.PresentManager;
import io.github.championash5357.naughtyornice.api.present.WrappedPresent;
import io.github.championash5357.naughtyornice.common.block.PresentBlock;
import io.github.championash5357.naughtyornice.common.init.GeneralRegistrar;
import io.github.championash5357.naughtyornice.common.tileentity.PresentTileEntity;
import io.github.championash5357.naughtyornice.common.util.LocalizationStrings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

/**
 * A basic implementation of {@link INiceness}.
 * Used for the niceness capability on players.
 */
public class Niceness implements INiceness {
	
	private static final Logger LOGGER = LogManager.getLogger();
	@Nullable
	private final PlayerEntity player;
	private double minNiceness, maxNiceness, centralNiceness; //Should be effectively final
	private int timer, timingThreshold, checkTimer;
	private double niceness, updateValue;
	
	@Nullable
	private WrappedPresent<?, ?> present;
	@Nullable
	private BlockPos presentPos;

	public Niceness() {
		this(null);
	}

	public Niceness(@Nullable final PlayerEntity player) {
		this(player, -100, 100);
	}

	public Niceness(@Nullable final PlayerEntity player, final double minNiceness, final double maxNiceness) {
		this(player, minNiceness, maxNiceness, -10);
	}

	public Niceness(@Nullable final PlayerEntity player, final double minNiceness, final double maxNiceness, final double centralNiceness) {
		this(player, 0, minNiceness, maxNiceness, centralNiceness);
	}

	public Niceness(@Nullable final PlayerEntity player, final double niceness, final double minNiceness, final double maxNiceness, final double centralNiceness) {
		this.player = player;
		this.niceness = niceness;
		this.minNiceness = minNiceness;
		this.maxNiceness = maxNiceness;
		this.centralNiceness = centralNiceness;
		this.timingThreshold = -2400;
		this.updateValue = -0.2;
	}

	@Override
	public double getNiceness() {
		return this.niceness + this.getLuckModifier();
	}
	
	private double getLuckModifier() {
		return this.player.getLuck();
	}

	@Override
	public void getNiceness(PlayerEntity interactedPlayer) {
		interactedPlayer.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> {
			if(this.checkTimer == 0) {
				this.player.sendMessage(new TranslationTextComponent(LocalizationStrings.NICENESS_CHECK, interactedPlayer.getDisplayName(), new StringTextComponent(String.format("%.2f", inst.getNiceness())).mergeStyle(TextFormatting.GOLD)), Util.DUMMY_UUID);
				this.checkTimer = 2400;
			} else {
				this.player.sendMessage(new TranslationTextComponent(LocalizationStrings.NICENESS_CHECK_ERROR, new StringTextComponent(String.format("%d", this.checkTimer)).mergeStyle(TextFormatting.GOLD)), Util.DUMMY_UUID);
			}
		});
	}
	
	@Override
	public boolean openPresent(PresentTileEntity te) {
		if(player == null || player.world.isRemote) return false;
		World world = this.player.world;
		if(this.presentPos != null) {
			BlockState state = world.getBlockState(this.presentPos);
			if(state.getBlock() instanceof PresentBlock && state.get(PresentBlock.OPEN)) return false;
			else {
				this.present = null;
				this.presentPos = null;
			}
		}
		if(this.present != null) return false;
		if(te.getNiceness() > 0 && this.getNiceness() - te.getNiceness() <= 0) return false;
		Optional<WrappedPresent<?, ?>> opt = PresentManager.getInstance().getWrappedPresent(te.getNiceness());
		if(!opt.isPresent()) return false;
		if(te.getNiceness() > 0) this.changeNiceness(this.getLuckModifier() - te.getNiceness());
		this.present = opt.get();
		this.presentPos = te.getPos();
		te.setEntity(this.player);
		world.setBlockState(this.presentPos, te.getBlockState().with(PresentBlock.OPEN, true));
		world.playSound((PlayerEntity) null, this.presentPos, GeneralRegistrar.BLOCK_PRESENT_OPEN.get(), SoundCategory.BLOCKS, 1.0f, 1.0f);
		return true;
	}
	
	@Override
	public void unwrap() {
		if(player == null || player.world.isRemote) return;
		this.present.give((ServerPlayerEntity) this.player, this.presentPos)
		.promotePartial(Util.func_240982_a_("The specified present could not be fully opened: " + PresentManager.getInstance().reversePresent(this.present).toString() + "\n", LOGGER::error));
		this.present = null;
		this.presentPos = null;
	}

	@Override
	public void setNiceness(double niceness, boolean overrideChecks) {
		if(!overrideChecks) if(player == null || player.world.isRemote || !((ServerPlayerEntity) player).interactionManager.survivalOrAdventure()) return;
		this.niceness = MathHelper.clamp(niceness, this.minNiceness, this.maxNiceness);
		this.updateThreshold();
	}

	@Override
	public void changeNiceness(double amount, boolean overrideChecks) {
		this.setNiceness(this.niceness + amount, overrideChecks);
	}

	@Override
	public void tick() {
		if(this.player.world.isRemote) return;
		if(this.timingThreshold != 0) {
			this.timer++;
			if(this.timer >= Math.abs(this.timingThreshold)) {
				this.changeNiceness(this.updateValue);
				this.timer = 0;
			}
		}
		this.checkTimer = Math.max(this.checkTimer - 1, 0);
	}

	private void updateThreshold() {
		if(this.niceness < this.centralNiceness && this.timingThreshold <= 0) {
			this.timer = 0;
			this.timingThreshold = 24000;
			this.updateValue = 1;
		} else if(this.niceness > this.centralNiceness && this.timingThreshold >= 0) {
			this.timer = 0;
			this.timingThreshold = -2400;
			this.updateValue = -0.2;
		} else if(this.niceness == this.centralNiceness) {
			this.timer = 0;
			this.timingThreshold = 0;
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("minNiceness", this.minNiceness);
		nbt.putDouble("maxNiceness", this.maxNiceness);
		nbt.putDouble("centralNiceness", this.centralNiceness);
		nbt.putDouble("niceness", this.niceness);
		nbt.putInt("timer", this.timer);
		nbt.putInt("timingThreshold", this.timingThreshold);
		nbt.putInt("checkTimer", this.checkTimer);
		nbt.putDouble("updateValue", this.updateValue);
		if(this.present != null && this.presentPos != null) {
			nbt.putString("present", PresentManager.getInstance().reversePresent(this.present).toString());
			nbt.putLong("pos", this.presentPos.toLong());
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.minNiceness = nbt.getDouble("minNiceness");
		this.maxNiceness = nbt.getDouble("maxNiceness");
		this.centralNiceness = nbt.getDouble("centralNiceness");
		this.niceness = nbt.getDouble("niceness");	
		this.timer = nbt.getInt("timer");
		this.timingThreshold = nbt.getInt("timingThreshold");
		this.checkTimer = nbt.getInt("checkTimer");
		this.updateValue = nbt.getDouble("updateValue");
		if(nbt.contains("present") && nbt.contains("pos")) {
			this.present = PresentManager.getInstance().getWrappedPresent(new ResourceLocation(nbt.getString("present")));
			this.presentPos = BlockPos.fromLong(nbt.getLong("pos"));
		} else {
			this.present = null;
			this.presentPos = null;
		}
	}
}
