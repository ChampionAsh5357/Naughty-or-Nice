package io.github.championash5357.naughtyornice.common.util;

import net.minecraft.util.IStringSerializable;

public enum LootSpawnLocation implements IStringSerializable {
	GROUND,
	INVENTORY;

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