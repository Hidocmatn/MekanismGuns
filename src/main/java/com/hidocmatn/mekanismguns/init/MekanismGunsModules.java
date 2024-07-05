package com.hidocmatn.mekanismguns.init;

import com.hidocmatn.mekanismguns.MekanismGuns;
import com.hidocmatn.mekanismguns.item.module.shared.ModuleCoilAccelerator;
import com.hidocmatn.mekanismguns.item.module.mekarifle.ModuleMarksmanBarrel;
import com.hidocmatn.mekanismguns.item.module.mekarifle.ModuleShortBarrel;
import mekanism.common.registration.impl.ModuleDeferredRegister;
import mekanism.common.registration.impl.ModuleRegistryObject;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MekanismGunsModules {
    private static final List<String> CONVERSION_KIT_SUFFIX_LIST = new ArrayList<>();

    public static final ModuleDeferredRegister MODULES = new ModuleDeferredRegister(MekanismGuns.MODID);

    public static final ModuleRegistryObject<ModuleCoilAccelerator> COIL_ACCELERATOR_UNIT = MODULES.register("coil_accelerator_unit", ModuleCoilAccelerator::new,
            () -> MekanismGunsItems.MODULE_COIL_ACCELERATOR.asItem(), builder -> builder.maxStackSize(4).rendersHUD());

    public static final ModuleRegistryObject<ModuleMarksmanBarrel> MARKSMAN_BARREL_UNIT = MODULES.register("marksman_barrel_unit", ModuleMarksmanBarrel::new,
            () -> MekanismGunsItems.MODULE_MARKSMAN_BARREL.asItem(), builder -> builder.maxStackSize(1).disabledByDefault().exclusive(256));

    public static final ModuleRegistryObject<ModuleShortBarrel> SHORT_BARREL_UNIT = MODULES.register("short_barrel_unit", ModuleShortBarrel::new,
            () -> MekanismGunsItems.MODULE_SHORT_BARREL.asItem(), builder -> builder.maxStackSize(1).disabledByDefault().exclusive(256) );

    public static void registerConversionSuffix(String suffix) {
        CONVERSION_KIT_SUFFIX_LIST.add(suffix);
    }

    public static boolean isGunIdRelocated(ResourceLocation gunId) {
        for (String suffix : CONVERSION_KIT_SUFFIX_LIST) {
            if (gunId.getPath().endsWith("_" + suffix)) {
                return true;
            }
        }
        return false;
    }

    public static ResourceLocation getOriginalGunId(ResourceLocation gunId) {
        for (String suffix : CONVERSION_KIT_SUFFIX_LIST) {
            if (gunId.getPath().endsWith("_" + suffix)) {
                String path = gunId.getPath();
                return gunId.withPath(path.substring(0, path.length() - suffix.length() - 1));
            }
        }
        //If gunId doesn't ends with any conversion suffix, return gunId itself.
        return gunId;
    }
}
