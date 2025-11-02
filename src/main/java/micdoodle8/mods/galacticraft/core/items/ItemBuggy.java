/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.GCFluids;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockLandingPadFull;
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy;
import micdoodle8.mods.galacticraft.core.tile.TileEntityBuggyFueler;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class ItemBuggy extends Item implements IHoldableItem, ISortableItem, GCRarity
{

    public ItemBuggy(String assetName)
    {
        super();
        this.setTranslationKey(assetName);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
        if (tab == GalacticraftCore.galacticraftItemsTab || tab == CreativeTabs.SEARCH)
        {
            for (int i = 0; i < 4; i++)
            {
                list.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = playerIn.getHeldItem(hand);
        boolean padFound = false;
        TileEntity tile = null;
        float centerX = -1;
        float centerY = -1;
        float centerZ = -1;

        if (worldIn.isRemote && playerIn instanceof EntityPlayerSP)
        {
            return EnumActionResult.PASS;
        }
        else
        {
            for (int i = -1; i < 2; i++)
            {
                for (int j = -1; j < 2; j++)
                {
                    BlockPos pos1 = pos.add(i, 0, j);
                    IBlockState state = worldIn.getBlockState(pos1);
                    Block id = state.getBlock();

                    if (id == GCBlocks.landingPadFull && state.getValue(BlockLandingPadFull.PAD_TYPE) == BlockLandingPadFull.EnumLandingPadFullType.BUGGY_PAD)
                    {
                        padFound = true;
                        tile = worldIn.getTileEntity(pos.add(i, 0, j));

                        centerX = pos.getX() + i + 0.5F;
                        centerY = pos.getY() + 0.4F;
                        centerZ = pos.getZ() + j + 0.5F;

                        break;
                    }
                }

                if (padFound)
                {
                    break;
                }
            }

            if (padFound)
            {
                if (!placeBuggyOnPad(stack, playerIn.getHorizontalFacing().getOpposite(), worldIn, tile, centerX, centerY, centerZ))
                {
                    return EnumActionResult.FAIL;
                }

                if (!playerIn.capabilities.isCreativeMode)
                {
                    stack.shrink(1);
                }
                return EnumActionResult.SUCCESS;
            }
            else
            {
                return EnumActionResult.PASS;
            }
        }
    }

    public static boolean placeBuggyOnPad(ItemStack stack, EnumFacing enumFacing, World worldIn, TileEntity tile, float centerX, float centerY, float centerZ)
    {
        // Check whether there is already a buggy on the pad
        if (tile instanceof TileEntityBuggyFueler)
        {
            if (((TileEntityBuggyFueler) tile).getDockedEntity() != null)
            {
                return false;
            }
        }
        else
        {
            return false;
        }

        EntityBuggy buggy = new EntityBuggy(worldIn, centerX, centerY, centerZ, stack.getItemDamage());
        buggy.setPosition(buggy.posX, buggy.posY, buggy.posZ);
        buggy.setRotation(enumFacing.getHorizontalAngle(), 0);
        worldIn.spawnEntity(buggy);

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BuggyFuel"))
        {
            buggy.buggyFuelTank.setFluid(new FluidStack(GCFluids.fluidFuel, stack.getTagCompound().getInteger("BuggyFuel")));
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (par1ItemStack.getItemDamage() != 0)
        {
            tooltip.add(GCCoreUtil.translate("gui.buggy.storage_space") + ": " + par1ItemStack.getItemDamage() * 18);
        }

        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("BuggyFuel"))
        {
            tooltip.add(GCCoreUtil.translate("gui.message.fuel.name") + ": " + par1ItemStack.getTagCompound().getInteger("BuggyFuel") + " / " + EntityBuggy.tankCapacity);
        }
    }

    @Override
    public boolean shouldHoldLeftHandUp(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean shouldHoldRightHandUp(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean shouldCrouch(EntityPlayer player)
    {
        return true;
    }

    @Override
    public EnumSortCategoryItem getCategory(int meta)
    {
        return EnumSortCategoryItem.GENERAL;
    }
}
