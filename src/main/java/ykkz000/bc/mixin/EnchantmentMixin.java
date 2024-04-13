/*
 * Better Combat
 * Copyright (C) 2024  ykkz000
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

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Unique
    private static final Set<Pair<Identifier, Identifier>> EXTRA_ACCEPTABLE_ITEMS = new HashSet<>();
    static {
        EXTRA_ACCEPTABLE_ITEMS.add(new Pair<>(new Identifier("minecraft", "bow"), new Identifier("minecraft", "piercing")));
        EXTRA_ACCEPTABLE_ITEMS.add(new Pair<>(new Identifier("minecraft", "bow"), new Identifier("minecraft", "quick_charge")));
    }
    @Inject(method = "isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Identifier itemId = getItemId(stack.getItem());
        Identifier enchantmentId = getEnchantmentId((Enchantment) (Object) this);
        for (Pair<Identifier, Identifier> pair : EXTRA_ACCEPTABLE_ITEMS) {
            if (pair.getLeft().equals(itemId) && pair.getRight().equals(enchantmentId)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
    @Unique
    private static Identifier getItemId(Item item) {
        return Registries.ITEM.getId(item);
    }

    @Unique
    private static Identifier getEnchantmentId(Enchantment enchantment) {
        return Registries.ENCHANTMENT.getId(enchantment);
    }
}
