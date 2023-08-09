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

package ykkz000.bc.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.*;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerInteractionManager.class)
@Environment(EnvType.CLIENT)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    private GameMode gameMode;
    /**
     * @author ykkz000
     * @reason Weapons should make reach-distance longer and empty hand should make reach-distance shorter.
     */
    @Overwrite
    public float getReachDistance() {
        float weaponDistance = 0.0f;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            ItemStack mainHandItemStack = player.getMainHandStack();
            Item mainHandItem = mainHandItemStack.getItem();
            if (mainHandItem instanceof SwordItem) {
                weaponDistance = 2.0f;
            } else if (mainHandItem instanceof AxeItem) {
                weaponDistance = 1.5f;
            } else if (mainHandItem instanceof PickaxeItem || mainHandItem instanceof ShovelItem || mainHandItem instanceof HoeItem) {
                weaponDistance = 1.0f;
            } else if (!mainHandItemStack.isEmpty()) {
                weaponDistance = 0.5f;
            }
        }
        return 3.5f + weaponDistance + (gameMode.isCreative() ? 0.5f : 0.0f);
    }
}
