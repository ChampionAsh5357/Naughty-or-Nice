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

package io.github.championash5357.naughtyornice.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import io.github.championash5357.naughtyornice.api.block.PresentBlock;
import io.github.championash5357.naughtyornice.api.tileentity.PresentTileEntity;
import io.github.championash5357.naughtyornice.client.ClientReference;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

//TODO: Lighting Effects
public class PresentTileEntityRenderer extends TileEntityRenderer<PresentTileEntity> {

	private static IBakedModel present_top;
	private static Minecraft mc;

	public PresentTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(PresentTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if(mc == null) {
			mc = Minecraft.getInstance();
		}
		if(present_top == null) {
			present_top = mc.getModelManager().getModel(ClientReference.PRESENT_TOP);
		}

		BlockState state = tileEntityIn.getBlockState();
		if(state.get(PresentBlock.OPEN)) {
			World world = tileEntityIn.getWorld();
			BlockPos pos = tileEntityIn.getPos();
			Vector3d vec = state.getOffset(world, pos);
			matrixStackIn.translate(vec.x, vec.y, vec.z);
			matrixStackIn.translate(0.5, 0, 0.5);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float) (tileEntityIn.getRotation(partialTicks) * 360)));
			matrixStackIn.translate(-0.5, tileEntityIn.getHeight(partialTicks), -0.5);
			matrixStackIn.push();
			renderBlock(state, world, pos, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
			matrixStackIn.pop();
		}
	}

	private void renderBlock(BlockState state, World world, BlockPos pos, MatrixStack matrixStackIn, IRenderTypeBuffer bufferTypeIn, int combinedLightIn, int combinedOverlayIn, IModelData modelData) {
		int i = mc.getBlockColors().getColor(state, world, pos, 0);
		float f = (float)(i >> 16 & 255) / 255.0F;
		float f1 = (float)(i >> 8 & 255) / 255.0F;
		float f2 = (float)(i & 255) / 255.0F;
		mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModel(matrixStackIn.getLast(), bufferTypeIn.getBuffer(RenderTypeLookup.func_239220_a_(state, false)), state, present_top, f, f1, f2, combinedLightIn, combinedOverlayIn, modelData);
	}
}
