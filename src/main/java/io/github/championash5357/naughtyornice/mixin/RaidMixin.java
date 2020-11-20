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

import java.util.Set;
import java.util.UUID;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.championash5357.naughtyornice.api.capability.CapabilityInstances;
import io.github.championash5357.naughtyornice.common.NaughtyOrNice;
import net.minecraft.entity.Entity;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.Raid.Status;
import net.minecraft.world.server.ServerWorld;

@Mixin(Raid.class)
public class RaidMixin {

	@Shadow private Set<UUID> heroes;
	@Shadow private Raid.Status status;
	@Shadow private ServerWorld world;
	
	@Inject(method = "stop()V", at = @At("HEAD"))
	private void handleNiceness(CallbackInfo ci) {
		if(this.status == Status.LOSS) {
			this.heroes.stream().map(this.world::getEntityByUuid).forEach(entity -> entity.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> inst.changeNiceness(NaughtyOrNice.getInstance().getNicenessManager().getGlobalEffects("raid_lose"))));
		} else if(this.status == Status.VICTORY) {
			this.heroes.stream().map(this.world::getEntityByUuid).forEach(entity -> entity.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> inst.changeNiceness(NaughtyOrNice.getInstance().getNicenessManager().getGlobalEffects("raid_win"))));
		}
	}
	
	@Overwrite
	public void addHero(Entity entity) {
		if(this.heroes.add(entity.getUniqueID())) {
			entity.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> inst.changeNiceness(NaughtyOrNice.getInstance().getNicenessManager().getGlobalEffects("raid_start")));
		}
	}
}
