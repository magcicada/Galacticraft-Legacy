/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.client.gui.overlay;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.util.ClientUtil;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

@SideOnly(Side.CLIENT)
public class OverlayOxygenTanks extends Overlay
{
    private static final ResourceLocation guiTexture = new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/gui.png");

    /**
     * Render the GUI that displays oxygen level in tanks
     */
    public static void renderOxygenTankIndicator(Minecraft mc, int heatLevel, int oxygenInTank1, int oxygenInTank2, boolean right, boolean top, boolean invalidThermal)
    {
        ScaledResolution scaledresolution = ClientUtil.getScaledRes(mc, mc.displayWidth, mc.displayHeight);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        mc.renderEngine.bindTexture(OverlayOxygenTanks.guiTexture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int leftX;
        int rightX;
        double bottomY;
        double topY;
        double zLevel = -190.0D;

        if (right)
        {
            leftX = i - 59;
            rightX = i - 39;
        }
        else
        {
            leftX = 10;
            rightX = 30;
        }

        if (top)
        {
            topY = 10.5;
        }
        else
        {
            topY = j - 57;
        }

        bottomY = topY + 46.5;

        double texMod = 1.0d / 256;
        drawModalRectWithCustomSizedTexture(leftX, (int) topY, zLevel, 66, 47, 9, 47, 256, 256);

        int heatLevelScaled = Math.min(Math.max(heatLevel, 1), 45);
        int heatLeveLScaledMax = Math.min(heatLevelScaled + 2, 45);
        int heatLevelScaledMin = Math.max(heatLeveLScaledMax - 2, 0);

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(leftX + 1, bottomY - heatLevelScaledMin, zLevel).tex(76 * texMod, (48 + 45 - heatLevelScaled) * texMod).endVertex();
        worldRenderer.pos(leftX + 8, bottomY - heatLevelScaledMin, zLevel).tex((76 + 7) * texMod, (48 + 45 - heatLevelScaled) * texMod).endVertex();
        worldRenderer.pos(leftX + 8, bottomY - heatLeveLScaledMax, zLevel).tex((76 + 7) * texMod, (48 + 45 - heatLevelScaled) * texMod).endVertex();
        worldRenderer.pos(leftX + 1, bottomY - heatLeveLScaledMax, zLevel).tex(76 * texMod, (48 + 45 - heatLevelScaled) * texMod).endVertex();
        tessellator.draw();

        leftX += 10;
        rightX += 10;

        drawModalRectWithCustomSizedTexture(leftX, (int) topY, zLevel, 85, 0, 19, 47, 256, 256);
        drawModalRectWithCustomSizedTexture(rightX, (int) topY, zLevel, 85, 0, 19, 47, 256, 256);
        GlStateManager.depthMask(true);
        int maxOxygen = 90;

        if (oxygenInTank1 > 0)
        {
            int oxygenLeft = oxygenInTank1 * 47 / maxOxygen;
            drawModalRectWithCustomSizedTexture(leftX, MathHelper.floor(topY + 1), zLevel, 104, Math.max(1, oxygenLeft), 19, Math.max(47, 47 - oxygenLeft), 256, 256);
        }
        else
        {
            drawModalRectWithCustomSizedTexture(leftX + 1, MathHelper.floor(topY + 1), zLevel, 105, 1, 17, 45, 256, 256);
        }

        if (oxygenInTank2 > 0)
        {
            int oxygenLeft = oxygenInTank2 * 47 / maxOxygen;
            drawModalRectWithCustomSizedTexture(rightX, MathHelper.floor(topY + 1), zLevel, 104, Math.max(1, oxygenLeft), 19, Math.max(47, 47 - oxygenLeft), 256, 256);
        }
        else
        {
            drawModalRectWithCustomSizedTexture(rightX + 1, MathHelper.floor(topY + 1), zLevel, 105, 1, 17, 45, 256, 256);
        }

        if (invalidThermal)
        {
            String value = GCCoreUtil.translate("gui.warning.invalid_thermal");
            mc.fontRenderer.drawString(value, leftX - 18 - mc.fontRenderer.getStringWidth(value), (int) bottomY - heatLevelScaled - mc.fontRenderer.FONT_HEIGHT / 2 - 1, ColorUtil.to32BitColor(255, 255, 10, 10));
        }
        GlStateManager.disableBlend();
    }

    private static void drawModalRectWithCustomSizedTexture(int x, int y, double zLevel, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, zLevel).tex(u * f, (v + (float) height) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, zLevel).tex((u + (float) width) * f, (v + (float) height) * f1).endVertex();
        bufferbuilder.pos(x + width, y, zLevel).tex((u + (float) width) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, zLevel).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }
}
