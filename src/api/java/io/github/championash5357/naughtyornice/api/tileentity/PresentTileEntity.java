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

package io.github.championash5357.naughtyornice.api.tileentity;

import io.github.championash5357.naughtyornice.api.block.PresentBlock;
import io.github.championash5357.naughtyornice.api.capability.CapabilityInstances;
import io.github.championash5357.naughtyornice.api.capability.INiceness;
import io.github.championash5357.naughtyornice.api.util.LocalizationStrings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Creates a basic tile entity instance.
 */
public class PresentTileEntity extends TileEntity implements ITickableTileEntity {

	private int niceness;

	private Entity entity;
	private double xValue;
	private double rotation, height;
	private double prevRotation, prevHeight;

	@SuppressWarnings("unchecked")
	public PresentTileEntity() {
		this((TileEntityType<? extends PresentTileEntity>) ForgeRegistries.TILE_ENTITIES.getValue(new ResourceLocation(LocalizationStrings.ID, "present")));
	}

	public PresentTileEntity(TileEntityType<? extends PresentTileEntity> ter) {
		super(ter);
	}

	private void unwrap() {
		if(!this.world.isRemote) {
			if(this.entity != null) this.entity.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(INiceness::unwrap);
			if(this.world.getBlockState(this.getPos()).equals(this.getBlockState())) this.world.setBlockState(this.getPos(), Blocks.AIR.getDefaultState(), BlockFlags.DEFAULT);
		}
		this.entity = null;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public boolean isOpening() {
		return this.entity != null;
	}

	public void setNiceness(int niceness) {
		this.niceness = niceness;
		this.markDirty();
	}

	public int getNiceness() {
		return this.niceness;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		this.niceness = nbt.getInt("niceness");
		if(nbt.contains("entityId")) this.entity = this.world.getEntityByID(nbt.getInt("entityId"));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putInt("niceness", this.niceness);
		if(this.entity != null) compound.putInt("entityId", this.entity.getEntityId());
		return compound;
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("niceness", this.niceness);
		return new SUpdateTileEntityPacket(this.getPos(), -1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.niceness = pkt.getNbtCompound().getInt("niceness");
		this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), BlockFlags.BLOCK_UPDATE);
	}

	@Override
	public void tick() {
		if(this.getBlockState().get(PresentBlock.OPEN)) {
			this.xValue += 0.01;
			this.prevRotation = this.rotation;
			this.rotation = Math.exp(2 * this.xValue) - 1;
			this.prevHeight = this.height;
			this.height = Math.max(0, Math.log10(this.rotation));
			if(!this.world.isRemote && this.height >= 0.6) {
				if(this.height >= 0.8) this.unwrap();
			} else if(this.height >= 0.75) {
				this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5, 1.0, 1.0, 1.0);
			}
			if(this.height >= 0.8) {
				this.xValue = 0;
				this.rotation = 0;
				this.height = 0;
			}
		}
	}

	public double getRotation(float partialTicks) {
		return this.rotation == 0 ? 0 : MathHelper.lerp(partialTicks, this.prevRotation, this.rotation);
	}

	public double getHeight(float partialTicks) {
		return this.height == 0 ? 0 : MathHelper.lerp(partialTicks, this.prevHeight, this.height);
	}
}
