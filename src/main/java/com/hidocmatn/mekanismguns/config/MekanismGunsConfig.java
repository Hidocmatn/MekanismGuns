package com.hidocmatn.mekanismguns.config;

import mekanism.common.config.MekanismConfigHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;

public class MekanismGunsConfig {
    public static final MekaGunConfig MEKA_GUN = new MekaGunConfig();
    public static void registerConfigs(ModLoadingContext modLoadingContext) {
        ModContainer modContainer = modLoadingContext.getActiveContainer();
        MekanismConfigHelper.registerConfig(modContainer, MEKA_GUN);
    }
}
