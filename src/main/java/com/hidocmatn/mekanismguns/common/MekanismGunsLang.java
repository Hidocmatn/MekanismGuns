package com.hidocmatn.mekanismguns.common;

import mekanism.api.text.ILangEntry;
import mekanism.common.Mekanism;
import net.minecraft.Util;

public class MekanismGunsLang implements ILangEntry {
    public static final MekanismGunsLang MODULE_COIL_ACCELERATOR = new MekanismGunsLang("module", "coil_boost");
    public static final MekanismGunsLang MODULE_COIL_DAMAGE_BOOST = new MekanismGunsLang("module", "coil_damage_boost");

    //Copied from MekanismLang
    private final String key;

    MekanismGunsLang(String type, String path) {
        this(Util.makeDescriptionId(type, Mekanism.rl(path)));
    }

    MekanismGunsLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}
