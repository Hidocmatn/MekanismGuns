package com.hidocmatn.mekanismguns;

import com.hidocmatn.mekanismguns.config.MekanismGunsConfig;
import com.hidocmatn.mekanismguns.init.MekanismGunsItems;
import com.hidocmatn.mekanismguns.init.MekanismGunsModules;
import com.hidocmatn.mekanismguns.item.module.mekarifle.ModuleMarksmanBarrel;
import com.mojang.logging.LogUtils;
import com.tacz.guns.api.resource.ResourceManager;
import mekanism.common.Mekanism;
import mekanism.common.registries.MekanismModules;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MekanismGuns.MODID)
public class MekanismGuns
{
    public static final String MODID = "mekanismguns";
    private static final Logger LOGGER = LogUtils.getLogger();
    public MekanismGuns() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MekanismGunsConfig.registerConfigs(ModLoadingContext.get());
        modEventBus.addListener(this::commonSetUp);
        modEventBus.addListener(this::imcQuene);
        MekanismGunsItems.ITEMS.register(modEventBus);
        MekanismGunsItems.ITEMS_MEKANISM.register(modEventBus);
        MekanismGunsModules.MODULES.register(modEventBus);
        registerAllConversionModules();
        //Automatic Gunpack Installation
        ResourceManager.registerExtraGunPack(MekanismGuns.class, "/custom/mekanismguns");
    }

    private void commonSetUp(FMLCommonSetupEvent event) {
//        MinecraftForge.EVENT_BUS.register(CommonPlayerTickHandler.class);
    }

    private void imcQuene(InterModEnqueueEvent event) {
        Mekanism.hooks.sendIMCMessages(event);
        MekanismGunsIMC.addMekaGunModules(MekanismModules.ENERGY_UNIT);
        MekanismGunsIMC.addMekaRifleModules(MekanismGunsModules.COIL_ACCELERATOR_UNIT);
        MekanismGunsIMC.addMekaRifleModules(MekanismGunsModules.MARKSMAN_BARREL_UNIT, MekanismGunsModules.SHORT_BARREL_UNIT);
    }

    private void registerAllConversionModules() {
        MekanismGunsModules.registerConversionSuffix(new ModuleMarksmanBarrel().getConvertedSuffix());
    }
}
