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

package ykkz000.bc.adjustment;

import net.minecraft.util.Identifier;
import ykkz000.bc.BetterCombat;
import ykkz000.bc.adjustment.player.*;
import ykkz000.bc.api.adjustment.AdjustmentRegister;
import ykkz000.bc.api.adjustment.BasePlayerAdjustment;

public final class BetterCombatAdjustments {
    public static final BasePlayerAdjustment HUNGER = register("hunger", new HungerAdjustment());
    public static final BasePlayerAdjustment LOW_HP = register("low_hp", new LowHPAdjustment());
    public static final BasePlayerAdjustment DIFFICULTY = register("difficulty", new DifficultyAdjustment());
    public static final BasePlayerAdjustment EXPERIENCE_LEVEL = register("experience_level", new ExperienceLevelAdjustment());
    public static final BasePlayerAdjustment HP_FIX = register("hp_fix", new HPFixAdjustment());

    private static BasePlayerAdjustment register(String id, BasePlayerAdjustment modifier) {
        return AdjustmentRegister.registerPlayerAdjustment(new Identifier(BetterCombat.MOD_ID, id), modifier);
    }

    public static void init() {}
}
