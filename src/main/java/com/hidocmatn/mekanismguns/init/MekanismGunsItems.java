package com.hidocmatn.mekanismguns.init;

import com.hidocmatn.mekanismguns.MekanismGuns;
import com.hidocmatn.mekanismguns.item.gun.MekaRifleGunItem;
import com.tacz.guns.api.item.gun.GunItemManager;
import mekanism.common.item.ItemModule;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MekanismGunsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MekanismGuns.MODID);
    public static final RegistryObject<MekaRifleGunItem> MEKA_RIFLE = ITEMS.register("meka_rifle",() -> new MekaRifleGunItem(new Item.Properties()));

    public static final ItemDeferredRegister ITEMS_MEKANISM = new ItemDeferredRegister(MekanismGuns.MODID);
    public static final ItemRegistryObject<ItemModule> MODULE_COIL_ACCELERATOR = ITEMS_MEKANISM.registerModule(MekanismGunsModules.COIL_ACCELERATOR_UNIT);
    public static final ItemRegistryObject<ItemModule> MODULE_MARKSMAN_BARREL = ITEMS_MEKANISM.registerModule(MekanismGunsModules.MARKSMAN_BARREL_UNIT);
    public static final ItemRegistryObject<ItemModule> MODULE_SHORT_BARREL = ITEMS_MEKANISM.registerModule(MekanismGunsModules.SHORT_BARREL_UNIT);

    //Register ItemType for tacz
    @SubscribeEvent
    public static void onItemRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.ITEMS.getRegistryKey())) {
            GunItemManager.registerGunItem(MekaRifleGunItem.TYPE_NAME, MEKA_RIFLE);
        }
    }
}
