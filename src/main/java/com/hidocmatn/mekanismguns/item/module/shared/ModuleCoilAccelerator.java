package com.hidocmatn.mekanismguns.item.module.shared;

import com.hidocmatn.mekanismguns.common.MekanismGunsLang;
import com.hidocmatn.mekanismguns.config.MekanismGunsConfig;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.text.EnumColor;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.config.value.CachedFloatValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

@ParametersAreNotNullByDefault
public class ModuleCoilAccelerator implements ICustomModule<ModuleCoilAccelerator> {
    //Used for Meka Rifles and Meka Snipers
    private IModuleConfigItem<Float> damageBoost;

    @Override
    public void init(IModule<ModuleCoilAccelerator> module, ModuleConfigItemCreator configItemCreator) {
//        damageBoost = configItemCreator.createConfigItem("damage_boost", MekanismGunsLang.MODULE_COIL_ACCELERATOR,
//                new ModuleEnumData<>(DamageBoost.OFF, module.getInstalledCount() + 2));
//        damageBoost = configItemCreator.createConfigItem("damage_boost", MekanismGunsLang.MODULE_COIL_ACCELERATOR, )
    }

    @Override
    public void addHUDStrings(IModule<ModuleCoilAccelerator> module, Player player, Consumer<Component> hudStringAdder) {
        if (module.isEnabled()) {
            hudStringAdder.accept(MekanismGunsLang.MODULE_COIL_DAMAGE_BOOST.translateColored(EnumColor.DARK_GRAY, EnumColor.RED, getDamageBoost()));
        }
    }

    public float getDamageBoost() {
        return damageBoost.get();
    }

    @NothingNullByDefault
    public enum DamageBoost implements IHasTextComponent {
        OFF(0.0F),
        LOW(MekanismGunsConfig.MEKA_GUN.mekaGunDefaultDamageBoostLow),
        MED(MekanismGunsConfig.MEKA_GUN.mekaGunDefaultDamageBoostMedium),
        HIGH(MekanismGunsConfig.MEKA_GUN.mekaGunDefaultDamageBoostHigh),
        EXTREME(MekanismGunsConfig.MEKA_GUN.mekaGunDefaultDamageBoostExtreme),
        MAX(MekanismGunsConfig.MEKA_GUN.mekaGunDefaultDamageBoostMax);

        private final float amount;
        private final Component label;

        DamageBoost(CachedFloatValue i) {
            this(i.get());
        }

        DamageBoost(float i) {
            this.amount = i;
            this.label = TextComponentUtil.getString(Float.toString(amount));
        }

        public float getAmount() {
            return amount;
        }

        @Override
        public Component getTextComponent() {
            return label;
        }
    }
}
