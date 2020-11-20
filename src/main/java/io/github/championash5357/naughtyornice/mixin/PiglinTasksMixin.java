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

import java.util.*;

import org.spongepowered.asm.mixin.*;

import io.github.championash5357.naughtyornice.api.capability.CapabilityInstances;
import io.github.championash5357.naughtyornice.common.NaughtyOrNice;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.monster.piglin.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Mixin(PiglinTasks.class)
public class PiglinTasksMixin {

	@Shadow(remap = false) private static boolean func_234454_D_(PiglinEntity piglin) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) public static Optional<LivingEntity> func_234515_g_(PiglinEntity piglin) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) private static boolean func_234506_e_(LivingEntity livingEntity) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) protected static void func_234487_b_(AbstractPiglinEntity piglin, LivingEntity livingEntity) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) private static boolean func_234535_v_(PiglinEntity piglin) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) private static void func_234521_i_(PiglinEntity piglin, LivingEntity livingEntity) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) private static void func_234516_g_(PiglinEntity piglin, LivingEntity livingEntity) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) protected static void func_234509_e_(AbstractPiglinEntity piglin, LivingEntity livingEntity) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) private static List<ItemStack> func_234524_k_(PiglinEntity piglin) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) private static void func_234475_a_(PiglinEntity piglin, List<ItemStack> stacks) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) private static void func_234498_c_(PiglinEntity piglin, ItemStack stack) { throw new RuntimeException("Can't believe this happened..."); }
	@Shadow(remap = false) protected static boolean func_234480_a_(Item item) { throw new RuntimeException("Can't believe this happened..."); }
	
	@Overwrite(remap = false)
	protected static void func_234468_a_(PiglinEntity piglin, LivingEntity tradingEntity) {
		if (!(tradingEntity instanceof PiglinEntity)) {
			if (func_234454_D_(piglin)) {
				handlePiglinBartering(piglin, tradingEntity, false);
			}

			Brain<PiglinEntity> brain = piglin.getBrain();
			brain.removeMemory(MemoryModuleType.CELEBRATE_LOCATION);
			brain.removeMemory(MemoryModuleType.DANCING);
			brain.removeMemory(MemoryModuleType.ADMIRING_ITEM);
			if (tradingEntity instanceof PlayerEntity) {
				brain.replaceMemory(MemoryModuleType.ADMIRING_DISABLED, true, 400L);
			}

			func_234515_g_(piglin).ifPresent((p_234462_2_) -> {
				if (p_234462_2_.getType() != tradingEntity.getType()) {
					brain.removeMemory(MemoryModuleType.AVOID_TARGET);
				}

			});
			if (piglin.isChild()) {
				brain.replaceMemory(MemoryModuleType.AVOID_TARGET, tradingEntity, 100L);
				if (func_234506_e_(tradingEntity)) {
					func_234487_b_(piglin, tradingEntity);
				}

			} else if (tradingEntity.getType() == EntityType.HOGLIN && func_234535_v_(piglin)) {
				func_234521_i_(piglin, tradingEntity);
				func_234516_g_(piglin, tradingEntity);
			} else {
				func_234509_e_(piglin, tradingEntity);
			}
		}
	}

	private static void handlePiglinBartering(PiglinEntity piglin, LivingEntity tradingEntity, boolean canTrade) {
		ItemStack itemstack = piglin.getHeldItem(Hand.OFF_HAND);
		piglin.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
		if (piglin.func_242337_eM()) {
			boolean flag = itemstack.isPiglinCurrency();
			if (canTrade && flag) {
				if(tradingEntity instanceof PlayerEntity)
					tradingEntity.getCapability(CapabilityInstances.NICENESS_CAPABILITY).ifPresent(inst -> inst.changeNiceness(NaughtyOrNice.getInstance().getNicenessManager().getGlobalEffects("trade")));
				func_234475_a_(piglin, func_234524_k_(piglin));
			} else if (!flag) {
				boolean flag1 = piglin.func_233665_g_(itemstack);
				if (!flag1) {
					func_234498_c_(piglin, itemstack);
				}
			}
		} else {
			boolean flag2 = piglin.func_233665_g_(itemstack);
			if (!flag2) {
				ItemStack itemstack1 = piglin.getHeldItemMainhand();
				if (func_234480_a_(itemstack1.getItem())) {
					func_234498_c_(piglin, itemstack1);
				} else {
					func_234475_a_(piglin, Collections.singletonList(itemstack1));
				}

				piglin.func_234438_m_(itemstack);
			}
		}
	}
}
