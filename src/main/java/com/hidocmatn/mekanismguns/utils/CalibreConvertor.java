package com.hidocmatn.mekanismguns.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface CalibreConvertor {
    String getConvertedSuffix();

    ResourceLocation getRelocatedGunId(@Nonnull ResourceLocation gunId);

    ResourceLocation getOriginalGunId(ResourceLocation gunId);

    void handleConversion(ItemStack gunItem);

    void undoConversion(ItemStack gunItem);
}
