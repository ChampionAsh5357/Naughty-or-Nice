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

package io.github.championash5357.naughtyornice.client;

import javax.annotation.Nullable;

import io.github.championash5357.naughtyornice.client.renderer.tileentity.PresentTileEntityRenderer;
import io.github.championash5357.naughtyornice.common.ISidedReference;
import io.github.championash5357.naughtyornice.common.NaughtyOrNice;
import io.github.championash5357.naughtyornice.common.init.GeneralRegistrar;
import io.github.championash5357.naughtyornice.common.tileentity.PresentTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientReference implements ISidedReference {

	public static final ResourceLocation PRESENT_TOP = new ResourceLocation(NaughtyOrNice.ID, "block/present_top");
	
	@Override
	public void setup(IEventBus mod, IEventBus forge) {
		mod.addListener(this::clientSetup);
		mod.addListener(this::blockColor);
		mod.addListener(this::itemColor);
		mod.addListener(this::modelRegistry);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		ClientRegistry.bindTileEntityRenderer(GeneralRegistrar.PRESENT_TYPE.get(), PresentTileEntityRenderer::new);
	}
	
	private void modelRegistry(final ModelRegistryEvent event) {
		ModelLoader.addSpecialModel(PRESENT_TOP);
	}
	
	private void blockColor(final ColorHandlerEvent.Block event) {
		event.getBlockColors().register((state, reader, pos, tintIndex) -> {
			if(tintIndex == 0) {
				@Nullable TileEntity te = reader.getTileEntity(pos);
				if(te != null && te instanceof PresentTileEntity) return ClientReference.getColor(((PresentTileEntity) te).getNiceness());
			}
			return 0;
		}, GeneralRegistrar.PRESENT.get());
	}

	private void itemColor(final ColorHandlerEvent.Item event) {
		event.getItemColors().register((stack, tintIndex) -> {
			if(tintIndex == 0) {
				CompoundNBT nbt = stack.getOrCreateChildTag("BlockEntityTag");
				return ClientReference.getColor(nbt.contains("niceness") ? nbt.getInt("niceness") : 0);
			}
			return 0;
		}, GeneralRegistrar.PRESENT.get());
	}

	public static int getColor(int niceness) {
		double temp = (niceness + 100) / 200.0, otherTemp = Math.max(0.0, niceness / 100.0);
		return (((int)((1 - otherTemp) * 0xFF) & 255) << 16) + (((int)(temp * 0xFF) & 255) << 8) + ((int)(otherTemp * 0xFF) & 255);
	}
}
