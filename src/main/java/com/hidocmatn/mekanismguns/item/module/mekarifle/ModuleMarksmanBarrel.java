package com.hidocmatn.mekanismguns.item.module.mekarifle;

import com.hidocmatn.mekanismguns.item.module.ConversionKitModule;
import mekanism.api.annotations.NothingNullByDefault;

@NothingNullByDefault
public class ModuleMarksmanBarrel extends ConversionKitModule<ModuleMarksmanBarrel> {
    public static final String SUFFIX = "marksman";

    @Override
    public String getConvertedSuffix() {
        return SUFFIX;
    }
}
