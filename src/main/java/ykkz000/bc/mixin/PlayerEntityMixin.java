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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ykkz000.bc.mixin.accessor.LivingEntityAccessor;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    /**
     * @author ykkz000
     * @reason Make it possible to double accumulating and Accumulating dissatisfaction damage is further reduced
     */
    @Redirect(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"))
    public float getAttackCooldownProgressForAttack(PlayerEntity instance, float baseTime) {
        float f = MathHelper.clamp((((LivingEntityAccessor) instance).getLastAttackedTicks()) / instance.getAttackCooldownProgressPerTick(), 0.0f, 2.0f);
        return f < 1.0f ? MathHelper.clamp(f * f, 0.0f, 1.0f) : MathHelper.clamp(1.5f * f - 0.5f, 1.0f, 2.0f);
    }
}
