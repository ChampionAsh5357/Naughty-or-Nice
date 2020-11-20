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

package io.github.championash5357.naughtyornice.api.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * An event that's called whenever the player heals an
 * entity. Called on the forge event bus.
 */
public class PlayerHealLivingEvent extends PlayerEvent {

	/**
	 * The healed entity.
	 */
	private final LivingEntity healedEntity;
	
	/**
	 * A simple constructor. Should never be called. For
	 * internal use.
	 * 
	 * @param player The current player
	 * @param healedEntity The healed entity
	 */
	public PlayerHealLivingEvent(PlayerEntity player, LivingEntity healedEntity) {
		super(player);
		this.healedEntity = healedEntity;
	}

	/**
	 * Gets the healed entity.
	 * 
	 * @return The healed entity
	 */
	public LivingEntity getHealedEntity() {
		return this.healedEntity;
	}
}
