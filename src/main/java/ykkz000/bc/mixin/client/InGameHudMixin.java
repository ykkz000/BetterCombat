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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.*;
import ykkz000.bc.mixin.accessor.LivingEntityAccessor;

@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public abstract class InGameHudMixin {
    @Final
    @Shadow
    private static Identifier CROSSHAIR_TEXTURE;
    @Final
    @Shadow
    private static Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE;
    @Final
    @Shadow
    private static Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE;
    @Final
    @Shadow
    private static Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE;
    @Final
    @Shadow
    private MinecraftClient client;
    @Final
    @Shadow
    private DebugHud debugHud;
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Shadow
    protected abstract boolean shouldRenderSpectatorCrosshair(HitResult hitResult);
    /**
     * @author ykkz000
     * @reason Draw double accumulating
     */
    @Overwrite
    private void renderCrosshair(DrawContext context) {
        GameOptions gameOptions = this.client.options;
        if (gameOptions.getPerspective().isFirstPerson() && this.client.interactionManager != null) {
            if (this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR || this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) {
                if (this.client.player != null && this.debugHud.shouldShowDebugHud() && !this.client.player.hasReducedDebugInfo() && !(Boolean)gameOptions.getReducedDebugInfo().getValue()) {
                    Camera camera = this.client.gameRenderer.getCamera();
                    MatrixStack matrixStack = RenderSystem.getModelViewStack();
                    matrixStack.push();
                    matrixStack.multiplyPositionMatrix(context.getMatrices().peek().getPositionMatrix());
                    matrixStack.translate((float)(this.scaledWidth / 2), (float)(this.scaledHeight / 2), 0.0F);
                    matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(camera.getPitch()));
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));
                    matrixStack.scale(-1.0F, -1.0F, -1.0F);
                    RenderSystem.applyModelViewMatrix();
                    RenderSystem.renderCrosshair(10);
                    matrixStack.pop();
                    RenderSystem.applyModelViewMatrix();
                } else {
                    RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
                    context.drawGuiTexture(CROSSHAIR_TEXTURE, (this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 15, 15);
                    if (this.client.options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
                        float f = 0;
                        if (this.client.player != null) {
                            f = getAttackCooldownProgressForDraw(this.client.player);
                        }
                        boolean bl = false;
                        if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && f >= 2.0F) {
                            bl = this.client.player.getAttackCooldownProgressPerTick() > 5.0F;
                            bl &= this.client.targetedEntity.isAlive();
                        }

                        int j = this.scaledHeight / 2 - 7 + 16;
                        int k = this.scaledWidth / 2 - 8;
                        if (bl) {
                            context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, k, j, 16, 16);
                        } else if (f < 1.0F) {
                            int l = (int)(f * 17.0F);
                            context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, k, j, 16, 4);
                            context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, k, j, l, 4);
                        } else if (f < 2.0F) {
                            int l = (int)((f - 1.0F) * 17.0F);
                            context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, k, j, 16, 4);
                            context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, k, j, 16, 4);
                            context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, k, j, l, 4);
                        }
                    }

                    RenderSystem.defaultBlendFunc();
                }

            }
        }
    }

    @Unique
    private static float getAttackCooldownProgressForDraw(PlayerEntity instance) {
        return MathHelper.clamp(((LivingEntityAccessor) instance).getLastAttackedTicks() / instance.getAttackCooldownProgressPerTick(), 0.0f, 2.0f);
    }
}
