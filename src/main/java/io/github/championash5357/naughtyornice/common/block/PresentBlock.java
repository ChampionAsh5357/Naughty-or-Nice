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

package io.github.championash5357.naughtyornice.common.block;

import io.github.championash5357.naughtyornice.api.capability.CapabilityInstances;
import io.github.championash5357.naughtyornice.api.capability.INiceness;
import io.github.championash5357.naughtyornice.common.tileentity.PresentTileEntity;
import io.github.championash5357.naughtyornice.common.util.Helper;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;

//TODO: Polish
public class PresentBlock extends Block implements IWaterLoggable {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty OPEN = BooleanProperty.create("open");
	private static final VoxelShape SHAPE_OPEN = Block.makeCuboidShape(4, 0, 4, 12, 6, 12);
	private static final VoxelShape SHAPE = VoxelShapes.or(Block.makeCuboidShape(4, 0, 4, 12, 6, 12), Block.makeCuboidShape(3.75, 5, 3.75, 12.25, 8, 12.25));

	public PresentBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, false).with(OPEN, false));
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(!worldIn.isRemote && player != null) {
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof PresentTileEntity && !((PresentTileEntity) te).isOpening()) {
				INiceness niceness = player.getCapability(CapabilityInstances.NICENESS_CAPABILITY).orElseThrow(() -> new IllegalStateException("Can't open a present without some niceness!"));
				return niceness.openPresent((PresentTileEntity) te) ? ActionResultType.func_233537_a_(worldIn.isRemote) : ActionResultType.FAIL;
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Vector3d vector3d = state.getOffset(worldIn, pos);
		return (state.get(OPEN) ? SHAPE_OPEN : SHAPE).withOffset(vector3d.x, vector3d.y, vector3d.z);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return stateIn;
	}

	@Override
	public OffsetType getOffsetType() {
		return OffsetType.XZ;
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		TileEntity te = worldIn.getTileEntity(pos);
		if(!worldIn.isRemote && te instanceof PresentTileEntity) {
			CompoundNBT nbt = stack.getOrCreateChildTag("BlockEntityTag");
			((PresentTileEntity) te).setNiceness(nbt.contains("niceness") ? nbt.getInt("niceness") : Helper.RANDOM.nextInt(201) - 100);
		}
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		ItemStack stack = super.getPickBlock(state, target, world, pos, player);
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof PresentTileEntity) {
			stack.getOrCreateChildTag("BlockEntityTag").putInt("niceness", ((PresentTileEntity) te).getNiceness());
		}
		return stack;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(OPEN, false).with(WATERLOGGED, Boolean.valueOf(context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(WATERLOGGED, OPEN);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new PresentTileEntity();
	}
}
