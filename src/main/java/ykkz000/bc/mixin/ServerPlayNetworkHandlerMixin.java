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

import com.mojang.datafixers.util.Function3;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Final
    @Shadow
    static Logger LOGGER;
    @Shadow
    public ServerPlayerEntity player;
    @Shadow
    @Nullable
    private Vec3d requestedTeleportPos;
    @Unique
    private static boolean canPlace1(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return (item instanceof BlockItem || item instanceof BucketItem) && !player.getItemCooldownManager().isCoolingDown(item);
    }
    @Shadow
    public abstract void disconnect(Text reason);

    /**
     * @author ykkz000
     * @reason Weapons should make reach-distance longer, so I disabled the reach-distance check
     */
    @Overwrite
    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, (ServerPlayNetworkHandler) (Object)this, this.player.getServerWorld());
        this.player.networkHandler.updateSequence(packet.getSequence());
        ServerWorld serverWorld = this.player.getServerWorld();
        Hand hand = packet.getHand();
        ItemStack itemStack = this.player.getStackInHand(hand);
        if (!itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
            return;
        }
        BlockHitResult blockHitResult = packet.getBlockHitResult();
        Vec3d vec3d = blockHitResult.getPos();
        BlockPos blockPos = blockHitResult.getBlockPos();
        Vec3d vec3d2 = Vec3d.ofCenter(blockPos);
        Vec3d vec3d3 = vec3d.subtract(vec3d2);
        if (!(Math.abs(vec3d3.getX()) < 1.0000001 && Math.abs(vec3d3.getY()) < 1.0000001 && Math.abs(vec3d3.getZ()) < 1.0000001)) {
            LOGGER.warn("Rejecting UseItemOnPacket from {}: Location {} too far away from hit block {}.", this.player.getGameProfile().getName(), vec3d, blockPos);
            return;
        }
        Direction direction = blockHitResult.getSide();
        this.player.updateLastActionTime();
        int i = this.player.getWorld().getTopY();
        if (blockPos.getY() < i) {
            if (this.requestedTeleportPos == null && this.player.squaredDistanceTo((double) blockPos.getX() + 0.5, (double) blockPos.getY() + 0.5, (double) blockPos.getZ() + 0.5) < 64.0 && serverWorld.canPlayerModifyAt(this.player, blockPos)) {
                ActionResult actionResult = this.player.interactionManager.interactBlock(this.player, serverWorld, itemStack, hand, blockHitResult);
                if (direction == Direction.UP && !actionResult.isAccepted() && blockPos.getY() >= i - 1 && canPlace1(this.player, itemStack)) {
                    MutableText text = Text.translatable("build.tooHigh", i - 1).formatted(Formatting.RED);
                    this.player.sendMessageToClient(text, true);
                } else if (actionResult.shouldSwingHand()) {
                    this.player.swingHand(hand, true);
                }
            }
        } else {
            MutableText text2 = Text.translatable("build.tooHigh", i - 1).formatted(Formatting.RED);
            this.player.sendMessageToClient(text2, true);
        }
        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
    }

    /**
     * @author ykkz000
     * @reason Weapons should make reach-distance longer, so I disabled the reach-distance check
     */
    @Overwrite
    public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, (ServerPlayNetworkHandler) (Object)this, this.player.getServerWorld());
        final ServerWorld serverWorld = this.player.getServerWorld();
        final Entity entity = packet.getEntity(serverWorld);
        this.player.updateLastActionTime();
        this.player.setSneaking(packet.isPlayerSneaking());
        if (entity != null) {
            if (!serverWorld.getWorldBorder().contains(entity.getBlockPos())) {
                return;
            }
            packet.handle(new PlayerInteractEntityC2SPacket.Handler() {

                private void processInteract(Hand hand, Function3<ServerPlayerEntity, Entity, Hand, ActionResult> action) {
                    ItemStack itemStack = player.getStackInHand(hand);
                    if (!itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
                        return;
                    }
                    ItemStack itemStack2 = itemStack.copy();
                    ActionResult actionResult = action.apply(player, entity, hand);
                    if (actionResult.isAccepted()) {
                        Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(player, itemStack2, entity);
                        if (actionResult.shouldSwingHand()) {
                            player.swingHand(hand, true);
                        }
                    }
                }

                @Override
                public void interact(Hand hand) {
                    this.processInteract(hand, PlayerEntity::interact);
                }

                @Override
                public void interactAt(Hand hand2, Vec3d pos) {
                    this.processInteract(hand2, (player, entity, hand) -> entity.interactAt(player, pos, hand));
                }

                @Override
                public void attack() {
                    if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof PersistentProjectileEntity || entity == player) {
                        disconnect(Text.translatable("multiplayer.disconnect.invalid_entity_attacked"));
                        LOGGER.warn("Player {} tried to attack an invalid entity", player.getName().getString());
                        return;
                    }
                    ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
                    if (!itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
                        return;
                    }
                    player.attack(entity);
                }
            });
        }
    }
}
