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

package ykkz000.bc.api.adjustment;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import ykkz000.bc.BetterCombat;
import ykkz000.bc.api.events.ServerPlayerTickEvents;

public class AdjustmentRegister {
    public static final RegistryKey<Registry<BasePlayerAdjustment>> PLAYER_ADJUSTMENT_REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier(BetterCombat.MOD_ID, "player"));
    public static final Registry<BasePlayerAdjustment> PLAYER_ADJUSTMENT_REGISTRY = FabricRegistryBuilder.createSimple(PLAYER_ADJUSTMENT_REGISTRY_KEY).buildAndRegister();

    public static BasePlayerAdjustment registerPlayerAdjustment(Identifier id, BasePlayerAdjustment modifier) {
        ServerPlayerTickEvents.END_TICK.register(player -> {
            if (modifier.shouldModify(player)) {
                modifier.modify(player);
            }
            return ActionResult.PASS;
        });
        return Registry.register(PLAYER_ADJUSTMENT_REGISTRY, id, modifier);
    }
}
