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

package io.github.championash5357.naughtyornice.common.util;

import net.minecraft.util.IStringSerializable;

public enum LootSpawnLocation implements IStringSerializable {
	GROUND,
	INVENTORY,
	RANDOM_INVENTORY,
	RANDOM_GROUND,
	BOOK_GROUND,
	BOOK_INVENTORY;

	public static LootSpawnLocation getValue(String str) {
		if(str == null) throw new NullPointerException("Inputted string is null.");
		for(LootSpawnLocation loc : LootSpawnLocation.values()) {
			if(loc.getString().equals(str)) return loc;
		}
		throw new IllegalArgumentException("No enum constant: " + str);
	}
	
	@Override
	public String getString() {
		return this.toString().toLowerCase();
	}
}