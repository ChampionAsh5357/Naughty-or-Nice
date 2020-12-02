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

package io.github.championash5357.naughtyornice.common.world.gen.feature.template;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.template.IPosRuleTests;
import net.minecraft.world.gen.feature.template.PosRuleTest;

public class AtYZeroTest extends PosRuleTest {

	public static final AtYZeroTest INSTANCE = new AtYZeroTest();
	public static final Codec<AtYZeroTest> CODEC = Codec.unit(() -> INSTANCE);
	
	private AtYZeroTest() {}

	@Override
	public boolean func_230385_a_(BlockPos rawPos, BlockPos actualPos, BlockPos seedPos, Random random) {
		return actualPos.getY() == 0;
	}

	@Override
	protected IPosRuleTests<?> func_230384_a_() {
		return null;
	}

}
