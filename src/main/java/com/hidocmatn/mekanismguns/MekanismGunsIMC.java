package com.hidocmatn.mekanismguns;

import mekanism.api.MekanismAPI;
import mekanism.api.providers.IModuleDataProvider;
import net.minecraftforge.fml.InterModComms;

public class MekanismGunsIMC {
    //Mostly copied from MekanismIMC
    public static final String ADD_MEKARIFLE_MODULES = "add_meka_rifle_modules";
    public static final String ADD_MEKASNIPER_MODULES = "add_meka_sniper_modules";

    public static void addMekaGunModules(IModuleDataProvider<?>... moduleDataProviders) {
        addMekaRifleModules(moduleDataProviders);
        addMekaSniperModules(moduleDataProviders);
    }

    public static void addMekaRifleAndSniperModules(IModuleDataProvider<?>... moduleDataProviders) {
        addMekaRifleModules(moduleDataProviders);
        addMekaSniperModules(moduleDataProviders);
    }

    public static void addMekaRifleModules(IModuleDataProvider<?>... moduleDataProviders) {
        sendModuleIMC(ADD_MEKARIFLE_MODULES, moduleDataProviders);
    }

    public static void addMekaSniperModules(IModuleDataProvider<?>... moduleDataProviders) {
        sendModuleIMC(ADD_MEKASNIPER_MODULES, moduleDataProviders);
    }

    private static void sendModuleIMC(String method, IModuleDataProvider<?>... moduleDataProviders) {
        if (moduleDataProviders == null || moduleDataProviders.length == 0) {
            throw new IllegalArgumentException("No module data providers given.");
        }
        InterModComms.sendTo(MekanismAPI.MEKANISM_MODID, method, () -> moduleDataProviders);
    }
}
