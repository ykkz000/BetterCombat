/*
 * Better Combat
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

package ykkz000.bc.adjustment.player;

import net.minecraft.server.network.ServerPlayerEntity;
import ykkz000.bc.api.adjustment.BasePlayerAdjustment;

public class HPFixAdjustment extends BasePlayerAdjustment {
    @Override
    public void modify(ServerPlayerEntity player) {
        player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
    }

    @Override
    public boolean shouldModify(ServerPlayerEntity player) {
        return true;
    }
}
