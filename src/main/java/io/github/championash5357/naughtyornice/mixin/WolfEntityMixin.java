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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.championash5357.naughtyornice.api.event.PlayerHealLivingEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntity {

	protected WolfEntityMixin(EntityType<? extends TameableEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Inject(method = "func_230254_b_(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResultType;",
			at = @At(value = "INVOKE", target = "heal(F)V"),
			remap = false)
	private void getNiceness(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResultType> ci) {
		MinecraftForge.EVENT_BUS.post(new PlayerHealLivingEvent(player, this));
	}
}
