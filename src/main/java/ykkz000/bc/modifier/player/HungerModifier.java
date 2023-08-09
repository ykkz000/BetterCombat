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

package ykkz000.bc.modifier.player;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import ykkz000.bc.modifier.Modifier;

public class HungerModifier implements Modifier<ServerPlayerEntity> {
    private int cooldown = 0;
    @Override
    public void modify(ServerPlayerEntity player) {
        cooldown = 80;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 100, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0));
    }

    @Override
    public boolean shouldModify(ServerPlayerEntity player) {
        cooldown = Math.max(cooldown - 1, 0);
        return cooldown == 0 && player.getHungerManager().getFoodLevel() < 6;
    }

}
