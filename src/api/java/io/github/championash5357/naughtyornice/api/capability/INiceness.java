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

import io.github.championash5357.naughtyornice.api.tileentity.PresentTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Niceness interface used for the capability. Handles all
 * logic related to the mod. 
 */
public interface INiceness extends INBTSerializable<CompoundNBT>{

	/**
	 * Gets the current niceness.
	 * 
	 * @return The niceness level
	 */
	double getNiceness();
	
	/**
	 * Gets the niceness of another player.
	 * 
	 * @param interactedPlayer The other player
	 */
	void getNiceness(PlayerEntity interactedPlayer);
	
	/**
	 * Sets the current niceness level. Is scaled
	 * between the minimum and maximum niceness.
	 * 
	 * @param niceness The new niceness level
	 */
	default void setNiceness(double niceness) { this.setNiceness(niceness, false); }
	
	/**
	 * Adds the current niceness level. Is scaled
	 * between the minimum and maximum niceness.
	 * 
	 * @param amount The amount to change niceness level
	 */
	default void changeNiceness(double amount) { this.changeNiceness(amount, false); }
	
	/**
	 * Sets the current niceness level. Is scaled
	 * between the minimum and maximum niceness.
	 * 
	 * @param niceness The new niceness level
	 * @param overrideChecks If any checks should be overridden. This includes if the player is alive or in a different gamemode.
	 */
	void setNiceness(double niceness, boolean overrideChecks);
	
	/**
	 * Adds the current niceness level. Is scaled
	 * between the minimum and maximum niceness.
	 * 
	 * @param amount The amount to change niceness level
	 * @param overrideChecks If any checks should be overridden. This includes if the player is alive or in a different gamemode.
	 */
	void changeNiceness(double amount, boolean overrideChecks);
	
	/**
	 * Opens the current present specified.
	 * 
	 * @param te The associated present tile entity
	 * @return If the present can be opened
	 */
	boolean openPresent(PresentTileEntity te);
	
	/**
	 * Executes the present information stored on the player.
	 */
	void unwrap();
	
	/**
	 * A tick method.
	 */
	void tick();
}
