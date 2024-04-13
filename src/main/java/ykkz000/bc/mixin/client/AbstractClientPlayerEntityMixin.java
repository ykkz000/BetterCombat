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

package ykkz000.bc.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import ykkz000.bc.util.BowUtil;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    /**
     * @author ykkz000
     * @reason Update FOV
     */
    @Overwrite
    public float getFovMultiplier() {
        float f = 1.0f;
        if (((AbstractClientPlayerEntity) (Object)this).getAbilities().flying) {
            f *= 1.1f;
        }
        if (((AbstractClientPlayerEntity) (Object)this).getAbilities().getWalkSpeed() == 0.0f || Float.isNaN(f *= ((float)((AbstractClientPlayerEntity) (Object)this).getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) / ((AbstractClientPlayerEntity) (Object)this).getAbilities().getWalkSpeed() + 1.0f) / 2.0f) || Float.isInfinite(f)) {
            f = 1.0f;
        }
        ItemStack itemStack = ((AbstractClientPlayerEntity) (Object)this).getActiveItem();
        if (((AbstractClientPlayerEntity) (Object)this).isUsingItem()) {
            if (itemStack.isOf(Items.BOW)) {
                int i = ((AbstractClientPlayerEntity) (Object)this).getItemUseTime();
                float g = ((float) i) / BowUtil.getPullTime(itemStack);
                g = g > 1.0f ? 1.0f : g * g;
                f *= 1.0f - g * 0.15f;
            } else if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && ((AbstractClientPlayerEntity) (Object)this).isUsingSpyglass()) {
                return 0.1f;
            }
        }
        return MathHelper.lerp(MinecraftClient.getInstance().options.getFovEffectScale().getValue().floatValue(), 1.0f, f);
    }
}
