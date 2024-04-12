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

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BowAttackGoal.class)
public abstract class BowAttackGoalMixin {
    @Shadow
    private int cooldown;
    @Shadow
    private int attackInterval;
    @Shadow
    private int targetSeeingTicker;
    @Shadow
    private int combatTicks;
    @Shadow
    private boolean movingToLeft;
    @Shadow
    private boolean backward;
    @Final
    @Shadow
    private float speed;
    @Final
    @Shadow
    private int squaredRange;
    @Final
    @Shadow
    private MobEntity actor;

    /**
     * @author ykkz000
     * @reason Make skeleton and stray entities able to shoot arrows with Quick Charge
     */
    @Overwrite
    public void tick() {
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity != null) {
            double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
            boolean bl2 = this.targetSeeingTicker > 0;
            if (bl != bl2) {
                this.targetSeeingTicker = 0;
            }

            if (bl) {
                ++this.targetSeeingTicker;
            } else {
                --this.targetSeeingTicker;
            }

            if (!(d > (double)this.squaredRange) && this.targetSeeingTicker >= 20) {
                this.actor.getNavigation().stop();
                ++this.combatTicks;
            } else {
                this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
                this.combatTicks = -1;
            }

            if (this.combatTicks >= 20) {
                if ((double)this.actor.getRandom().nextFloat() < 0.3) {
                    this.movingToLeft = !this.movingToLeft;
                }

                if ((double)this.actor.getRandom().nextFloat() < 0.3) {
                    this.backward = !this.backward;
                }

                this.combatTicks = 0;
            }

            if (this.combatTicks > -1) {
                if (d > (double)(this.squaredRange * 0.75F)) {
                    this.backward = false;
                } else if (d < (double)(this.squaredRange * 0.25F)) {
                    this.backward = true;
                }

                this.actor.getMoveControl().strafeTo(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
                Entity var7 = this.actor.getControllingVehicle();
                if (var7 instanceof MobEntity mobEntity) {
                    mobEntity.lookAtEntity(livingEntity, 30.0F, 30.0F);
                }

                this.actor.lookAtEntity(livingEntity, 30.0F, 30.0F);
            } else {
                this.actor.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
            }

            if (this.actor.isUsingItem()) {
                ItemStack itemStack = this.actor.getActiveItem();
                if (!bl && this.targetSeeingTicker < -60) {
                    this.actor.clearActiveItem();
                } else if (bl) {
                    int i = this.actor.getItemUseTime();
                    if (i >= BowItemMixin.getPullTime(itemStack)) {
                        this.actor.clearActiveItem();
                        ((RangedAttackMob)this.actor).shootAt(livingEntity, BowItemMixin.getPullProgress(i, itemStack));
                        this.cooldown = this.attackInterval;
                    }
                }
            } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
                this.actor.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.BOW));
            }

        }
    }
}
