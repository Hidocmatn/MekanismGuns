package com.hidocmatn.mekanismguns.item.module;

import com.hidocmatn.mekanismguns.init.MekanismGunsModules;
import com.hidocmatn.mekanismguns.item.gun.AbstractMekaGunItem;
import com.hidocmatn.mekanismguns.utils.AmmoUnloader;
import com.hidocmatn.mekanismguns.utils.CalibreConvertor;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Common Implemention for modules which may shift guns' calibre.
 */
public abstract class ConversionKitModule<MODULE extends ConversionKitModule<MODULE>> implements ICustomModule<MODULE>, CalibreConvertor {

    @Override
    public ResourceLocation getRelocatedGunId(@Nonnull ResourceLocation gunId) {
        if (!getConvertedSuffix().isEmpty()) {
            if (!MekanismGunsModules.isGunIdRelocated(gunId)) {
                return gunId.withSuffix("_" + getConvertedSuffix());
            }
        }
        //TODO: log error info
        //Return original gunId as a fallback;
        return gunId;
    }

    @Override
    public ResourceLocation getOriginalGunId(ResourceLocation gunId) {
        if (gunId.getPath().endsWith(getConvertedSuffix())) {
            String path = gunId.getPath();
            return gunId.withPath(path.substring(0, path.length() - getConvertedSuffix().length() - 1));
        } else {
            return MekanismGunsModules.getOriginalGunId(gunId);
        }
    }

    @Override
    public void handleConversion(ItemStack gunItem) {
        if (gunItem.getItem() instanceof AbstractMekaGunItem mekaGun) {
            ResourceLocation originalId = mekaGun.getGunId(gunItem);
            ResourceLocation relocatedId = getRelocatedGunId(originalId);
            if (originalId != null && !originalId.equals(relocatedId)) {
                mekaGun.setGunId(gunItem, relocatedId);
                mekaGun.initFireSelect(gunItem);
            }
        }
    }

    @Override
    public void undoConversion(ItemStack gunItem) {
        if (gunItem.getItem() instanceof AbstractMekaGunItem mekaGun) {
            ResourceLocation relocatedId = mekaGun.getGunId(gunItem);
            ResourceLocation originalId = getOriginalGunId(relocatedId);
            if (relocatedId != null && !relocatedId.equals(originalId)) {
                mekaGun.setGunId(gunItem, originalId);
                mekaGun.initFireSelect(gunItem);
            }
        }
    }

    //Unload ammo when module removed or enabled/disabled.
    @Override
    public void onAdded(IModule<MODULE> module, boolean first) {
        ItemStack gunItem = module.getContainer();
        if (module.isEnabled()) {
            if (first && (gunItem.getItem() instanceof AmmoUnloader mekaGun)) {
                mekaGun.recordPreviousGunId(gunItem);
                mekaGun.addUnloadAmmoMark(gunItem);
                handleConversion(gunItem);
            }
        }
    }

    @Override
    public void onRemoved(IModule<MODULE> module, boolean last) {
        ItemStack gunItem = module.getContainer();
        if (last && (gunItem.getItem() instanceof AmmoUnloader mekaGun)) {
            mekaGun.recordPreviousGunId(gunItem);
            mekaGun.addUnloadAmmoMark(gunItem);
            undoConversion(gunItem);
        }
    }

    @Override
    public void onEnabledStateChange(IModule<MODULE> module) {
        ItemStack gunItem = module.getContainer();
        if (gunItem.getItem() instanceof AmmoUnloader mekaGun) {
            if (module.isEnabled()) {
                handleConversion(gunItem);
            } else {
                mekaGun.recordPreviousGunId(gunItem);
                undoConversion(gunItem);
            }
            mekaGun.addUnloadAmmoMark(gunItem);
        }
    }
}
