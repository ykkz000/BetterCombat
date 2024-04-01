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

package ykkz000.bc.adjustment.player;

import com.mojang.logging.LogUtils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import ykkz000.bc.api.adjustment.BasePlayerAdjustment;
import ykkz000.bc.util.EntityUtil;

import java.util.UUID;

public class DifficultyAdjustment extends BasePlayerAdjustment {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final UUID ATTRIBUTE_MODIFIER_UUID = UUID.fromString("D56FD3FC-77D4-2067-73BE-5FBCC64D3614");
    @SuppressWarnings("FieldCanBeLocal")
    private final String ATTRIBUTE_MODIFIER_NAME = "difficulty";

    @Override
    public void modify(ServerPlayerEntity player) {
        boolean success = EntityUtil.refreshAttributeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH,
                ATTRIBUTE_MODIFIER_UUID, ATTRIBUTE_MODIFIER_NAME,
                false,
                player.getServerWorld().getDifficulty().getId() * -4.0,
                EntityAttributeModifier.Operation.ADDITION);
        if (!success) {
            LOGGER.warn("Cannot find player {}'s attribute EntityAttributes.GENERIC_MAX_HEALTH.", player.getName());
        }
    }

    @Override
    public boolean shouldModify(ServerPlayerEntity obj) {
        return true;
    }
}
