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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin {
    @Shadow
    protected abstract PersistentProjectileEntity createArrowProjectile(ItemStack stack, float pullProgress);
    /**
     * @author ykkz000
     * @reason Make skeleton and stray entities able to shoot arrows with Piercing
     */
    @Overwrite
    public void shootAt(LivingEntity target, float pullProgress) {
        ItemStack itemStack = ((AbstractSkeletonEntity) (Object)this).getProjectileType(((AbstractSkeletonEntity) (Object)this).getStackInHand(ProjectileUtil.getHandPossiblyHolding(((AbstractSkeletonEntity) (Object)this), Items.BOW)));
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack, pullProgress);
        double d = target.getX() - ((AbstractSkeletonEntity) (Object)this).getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - ((AbstractSkeletonEntity) (Object)this).getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6F, (float)(14 - ((AbstractSkeletonEntity) (Object)this).getWorld().getDifficulty().getId() * 4));
        int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, itemStack);
        if (i > 0) {
            persistentProjectileEntity.setPierceLevel((byte)i);
        }
        ((AbstractSkeletonEntity) (Object)this).playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (((AbstractSkeletonEntity) (Object)this).getRandom().nextFloat() * 0.4F + 0.8F));
        ((AbstractSkeletonEntity) (Object)this).getWorld().spawnEntity(persistentProjectileEntity);
    }
}
