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

package ykkz000.bc.mixin.events;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ykkz000.bc.api.events.ServerPlayerTickEvents;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "tick()V", at = @At("HEAD"))
    public void onStartTick(CallbackInfo ci) {
        ServerPlayerTickEvents.START_TICK.invoker().run((ServerPlayerEntity) (Object) this);
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    public void onEndTick(CallbackInfo ci) {
        ServerPlayerTickEvents.END_TICK.invoker().run((ServerPlayerEntity) (Object) this);
    }
}
