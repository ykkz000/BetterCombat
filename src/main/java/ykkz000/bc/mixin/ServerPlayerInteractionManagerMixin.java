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

package ykkz000.bc.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Final
    @Shadow
    private static Logger LOGGER;
    @Shadow
    protected ServerWorld world;
    @Final
    @Shadow
    protected ServerPlayerEntity player;
    @Shadow
    private GameMode gameMode;
    @Shadow
    private boolean mining;
    @Shadow
    private int startMiningTime;
    @Shadow
    private BlockPos miningPos;
    @Shadow
    private int tickCounter;
    @Shadow
    private boolean failedToMine;
    @Shadow
    private BlockPos failedMiningPos;
    @Shadow
    private int failedStartMiningTime;
    @Shadow
    private int blockBreakingProgress;
    @Shadow
    public abstract boolean isCreative();
    @Shadow
    public abstract void finishMining(BlockPos pos, int sequence, String reason);

    /**
     * @author ykkz000
     * @reason Weapons should make reach-distance longer, so I disabled the reach-distance check
     */
    @Overwrite
    public void processBlockBreakingAction(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence) {
        if (pos.getY() >= worldHeight) {
            this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, this.world.getBlockState(pos)));
            return;
        }
        if (action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
            if (!this.world.canPlayerModifyAt(this.player, pos)) {
                this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, this.world.getBlockState(pos)));
                return;
            }
            if (this.isCreative()) {
                this.finishMining(pos, sequence, "creative destroy");
                return;
            }
            if (this.player.isBlockBreakingRestricted(this.world, pos, this.gameMode)) {
                this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, this.world.getBlockState(pos)));
                return;
            }
            this.startMiningTime = this.tickCounter;
            float f = 1.0f;
            BlockState blockState = this.world.getBlockState(pos);
            if (!blockState.isAir()) {
                blockState.onBlockBreakStart(this.world, pos, this.player);
                f = blockState.calcBlockBreakingDelta(this.player, this.player.getWorld(), pos);
            }
            if (!blockState.isAir() && f >= 1.0f) {
                this.finishMining(pos, sequence, "insta mine");
            } else {
                if (this.mining) {
                    this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.miningPos, this.world.getBlockState(this.miningPos)));
                }
                this.mining = true;
                this.miningPos = pos.toImmutable();
                int i = (int)(f * 10.0f);
                this.world.setBlockBreakingInfo(this.player.getId(), pos, i);
                this.blockBreakingProgress = i;
            }
        } else if (action == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
            if (pos.equals(this.miningPos)) {
                int j = this.tickCounter - this.startMiningTime;
                BlockState blockState = this.world.getBlockState(pos);
                if (!blockState.isAir()) {
                    float g = blockState.calcBlockBreakingDelta(this.player, this.player.getWorld(), pos) * (float)(j + 1);
                    if (g >= 0.7f) {
                        this.mining = false;
                        this.world.setBlockBreakingInfo(this.player.getId(), pos, -1);
                        this.finishMining(pos, sequence, "destroyed");
                        return;
                    }
                    if (!this.failedToMine) {
                        this.mining = false;
                        this.failedToMine = true;
                        this.failedMiningPos = pos;
                        this.failedStartMiningTime = this.startMiningTime;
                    }
                }
            }
        } else if (action == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
            this.mining = false;
            if (!Objects.equals(this.miningPos, pos)) {
                LOGGER.warn("Mismatch in destroy block pos: {} {}", this.miningPos, pos);
                this.world.setBlockBreakingInfo(this.player.getId(), this.miningPos, -1);
            }
            this.world.setBlockBreakingInfo(this.player.getId(), pos, -1);
        }
    }
}
