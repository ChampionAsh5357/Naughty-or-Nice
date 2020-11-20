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

package io.github.championash5357.naughtyornice.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.championash5357.naughtyornice.api.capability.CapabilityInstances;
import io.github.championash5357.naughtyornice.common.NaughtyOrNice;
import net.minecraft.advancements.criterion.VillagerTradeTrigger;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(VillagerTradeTrigger.class)
public class VillagerTradeTriggerMixin {

	@Inject(method = "test(Lnet/minecraft/entity/player/ServerPlayerEntity;Lnet/minecraft/entity/merchant/villager/AbstractVillagerEntity;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
	private void giveNiceness(ServerPlayerEntity player, AbstractVillagerEntity villager, ItemStack stack, CallbackInfo ci) {
		player.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> inst.changeNiceness(NaughtyOrNice.getInstance().getNicenessManager().getGlobalEffects("trade")));
	}
}
