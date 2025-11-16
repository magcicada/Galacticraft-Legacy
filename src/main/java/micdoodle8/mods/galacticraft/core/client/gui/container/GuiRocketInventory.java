/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.client.gui.container;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion;
import micdoodle8.mods.galacticraft.core.inventory.ContainerRocketInventory;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRocketInventory extends GuiContainerGC
{
    private static final ResourceLocation[] rocketTextures = IntStream.rangeClosed(0, 4).boxed().map(integer -> new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/rocket_" + integer * 18 + ".png")).toArray(ResourceLocation[]::new);

    private final IInventory upperChestInventory;
    private final EntityTieredRocket tieredRocket;
    private final EnumRocketType rocketType;

    public GuiRocketInventory(IInventory playerInventory, EntityTieredRocket tieredRocket, EnumRocketType rocketType)
    {
        super(new ContainerRocketInventory(playerInventory, tieredRocket, rocketType, FMLClientHandler.instance().getClient().player));
        this.upperChestInventory = playerInventory;
        this.tieredRocket = tieredRocket;
        this.allowUserInput = false;
        this.ySize = rocketType.getInventorySpace() <= 3 ? 132 : 145 + rocketType.getInventorySpace() * 2;
        this.rocketType = rocketType;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        List<String> fuelTankDesc = new ArrayList<>();
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuel_tank.desc.0"));
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuel_tank.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + (this.tieredRocket.rocketType.getInventorySpace() == 2 ? 70 : 71),
            (this.height - this.ySize) / 2 + 6, 36, 40, fuelTankDesc, this.width, this.height, this));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.fuel.name"), 8, 2 + 3, 4210752);

        this.fontRenderer.drawString(GCCoreUtil.translate(this.upperChestInventory.getName()), 8, 34 + 2 + 3, 4210752);

        if (this.mc.player != null)
        {
            this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.fuel.name") + ":", 125, 15 + 3, 4210752);
            final double percentage = this.tieredRocket.getScaledFuelLevel(100);
            final String color = percentage > 80.0D ? EnumColor.BRIGHT_GREEN.getCode() : percentage > 40.0D ? EnumColor.ORANGE.getCode() : EnumColor.RED.getCode();
            final String str = percentage + "% " + GCCoreUtil.translate("gui.message.full.name");
            this.fontRenderer.drawString(color + str, 117 - str.length() / 2, 20 + 8, 4210752);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        this.mc.getTextureManager().bindTexture(GuiRocketInventory.rocketTextures[(this.rocketType.getInventorySpace() - 2) / 18]);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, 176, this.ySize);

        if (this.mc.player != null)
        {
            final int fuelLevel = this.tieredRocket.getScaledFuelLevel(38);

            this.drawTexturedModalRect((this.width - this.xSize) / 2 + (this.tieredRocket.rocketType.getInventorySpace() == 2 ? 71 : 72),
                (this.height - this.ySize) / 2 + 45 - fuelLevel, 176, 38 - fuelLevel, 42, fuelLevel);
        }
    }
}
