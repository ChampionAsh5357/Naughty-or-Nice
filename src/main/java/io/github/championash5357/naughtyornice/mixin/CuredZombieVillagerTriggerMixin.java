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
import net.minecraft.advancements.criterion.CuredZombieVillagerTrigger;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

@Mixin(CuredZombieVillagerTrigger.class)
public class CuredZombieVillagerTriggerMixin {

	@Inject(method = "trigger(Lnet/minecraft/entity/player/ServerPlayerEntity;Lnet/minecraft/entity/monster/ZombieEntity;Lnet/minecraft/entity/merchant/villager/VillagerEntity;)V", at = @At("HEAD"))
	private void giveNiceness(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager, CallbackInfo ci) {
		player.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> inst.changeNiceness(NaughtyOrNice.getInstance().getNicenessManager().getGlobalEffects("cure")));
	}
}
