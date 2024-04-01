/*
 * BC
 * Copyright (C) 2023  ykkz000
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ykkz000.bc.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShieldItem.class)
public abstract class ShieldItemMixin extends Item {
    @Unique
    private static final int MAX_USE_TIME = 80;

    public ShieldItemMixin(Settings settings) {
        super(settings);
    }

    /**
     * @author ykkz000
     * @reason Decrease the using time of the shield
     */
    @Override
    @Overwrite
    public int getMaxUseTime(ItemStack stack) {
        return MAX_USE_TIME;
    }

    @Override
    @Intrinsic(displace = true)
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set((ShieldItem) (Object) this, getCoolDownTick(0));
        }
        return stack;
    }

    @Override
    @Intrinsic(displace = true)
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set((ShieldItem) (Object) this, getCoolDownTick(remainingUseTicks));
        }
    }

    @Unique
    private static int getCoolDownTick(int remainingUseTicks){
        return 40 - 20 * remainingUseTicks / MAX_USE_TIME;
    }
}
