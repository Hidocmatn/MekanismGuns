package com.hidocmatn.mekanismguns.item.module.mekarifle;

import com.hidocmatn.mekanismguns.item.module.ConversionKitModule;
import mekanism.api.annotations.NothingNullByDefault;

@NothingNullByDefault
public class ModuleShortBarrel extends ConversionKitModule<ModuleShortBarrel> {
    public static final String SUFFIX = "carbine";

    @Override
    public String getConvertedSuffix() {
        return SUFFIX;
    }
}
