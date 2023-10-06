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

package ykkz000.bc.modifier;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import ykkz000.bc.api.events.ServerPlayerTickEvents;
import ykkz000.bc.modifier.player.*;

import java.util.ArrayList;
import java.util.Collection;

public class Modifiers {
    private static final Collection<Modifier<ServerPlayerEntity>> PLAYER_MODIFIERS = new ArrayList<>();

    static {
        PLAYER_MODIFIERS.add(new HungerModifier());
        PLAYER_MODIFIERS.add(new LowHPModifier());
        PLAYER_MODIFIERS.add(new DifficultyModifier());
        PLAYER_MODIFIERS.add(new ExperienceLevelModifier());
        PLAYER_MODIFIERS.add(new HPFixModifier());
    }
    public static void initializeModifiers() {
        for (Modifier<ServerPlayerEntity> modifier : PLAYER_MODIFIERS) {
            ServerPlayerTickEvents.END_TICK.register(player -> {
                if (modifier.shouldModify(player)) {
                    modifier.modify(player);
                }
                return ActionResult.PASS;
            });
        }
    }
}
