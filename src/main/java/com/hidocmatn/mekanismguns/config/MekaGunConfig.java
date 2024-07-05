package com.hidocmatn.mekanismguns.config;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedFloatValue;
import mekanism.common.config.value.CachedFloatingLongValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class MekaGunConfig extends BaseMekanismConfig
{
    //Mostly copied from GearConfig
    private static final String MEKAGUN_DEFAULT_CATEGORY = "meka_gun_default";
    private static final String MEKARIFLE_CATEGORY = "meka_rifle";
    private final ForgeConfigSpec configSpec;

    //Mekagun default settings
    public final CachedFloatingLongValue mekaGunDefaultEnergyUsageTrigger;
    public final CachedFloatingLongValue mekaGunDefaultEnergyUsageCoil;
    public final CachedFloatValue mekaGunDefaultDamageBoostLow;
    public final CachedFloatValue mekaGunDefaultDamageBoostMedium;
    public final CachedFloatValue mekaGunDefaultDamageBoostHigh;
    public final CachedFloatValue mekaGunDefaultDamageBoostExtreme;
    public final CachedFloatValue mekaGunDefaultDamageBoostMax;
    public final CachedFloatingLongValue mekaGunDefaultBaseEnergyCapability;
    public final CachedFloatingLongValue mekaGunDefaultBaseChargeRate;
    //Meka-Rifle settings
    public final CachedFloatingLongValue mekaRifleBaseEnergyCapability;
    public final CachedFloatingLongValue mekaRifleBaseChargeRate;

    MekaGunConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Default Settings").push(MEKAGUN_DEFAULT_CATEGORY);
        mekaGunDefaultEnergyUsageTrigger = CachedFloatingLongValue
                .define(
                        this,
                        builder,
                        "Trigger energy (Joules) usage of the Meka-Guns.",
                        "energyUsageTrigger",
                        FloatingLong.createConst(1_000)
                );
        mekaGunDefaultEnergyUsageCoil =CachedFloatingLongValue
                .define(
                        this,
                        builder,
                        "Base energy (Joules) usage for coil accelerator of the Meka-Guns.",
                        "energyUsageCoil",
                        FloatingLong.createConst(2_000));
        mekaGunDefaultDamageBoostLow = CachedFloatValue
                .wrap(
                        this,
                        builder
                                .comment("Default damage boost amount of Coil Accelerator Unit")
                                .defineInRange("damageBoostLow", 2.0F, 1.0F, Float.MAX_VALUE)
                );
        mekaGunDefaultDamageBoostMedium = CachedFloatValue
                .wrap(
                        this,
                        builder
                                .comment("Default damage boost amount of Coil Accelerator Unit")
                                .defineInRange("damageBoostMedium", 6.0F, 1.0F, Float.MAX_VALUE)
                );
        mekaGunDefaultDamageBoostHigh = CachedFloatValue
                .wrap(
                        this,
                        builder
                                .comment("Default damage boost amount of Coil Accelerator Unit")
                                .defineInRange("damageBoostHigh", 11.0F, 1.0F, Float.MAX_VALUE)
                );
        mekaGunDefaultDamageBoostExtreme = CachedFloatValue
                .wrap(
                        this,
                        builder
                                .comment("Default damage boost amount of Coil Accelerator Unit")
                                .defineInRange("damageBoostExtreme", 15.0F, 1.0F, Float.MAX_VALUE)
                );
        mekaGunDefaultDamageBoostMax = CachedFloatValue
                .wrap(
                        this,
                        builder
                                .comment("Default damage boost amount of Coil Accelerator Unit")
                                .defineInRange("damageBoostMax", 18.0F, 1.0F, Float.MAX_VALUE)
                );
        mekaGunDefaultBaseEnergyCapability = CachedFloatingLongValue
                .define(
                        this,
                        builder,
                        "Default Energy capacity (Joules) of Meka-Guns without any installed upgrades. Quadratically scaled by upgrades.",
                        "baseEnergyCapability",
                        FloatingLong.createConst(16_000_000)
                );
        mekaGunDefaultBaseChargeRate = CachedFloatingLongValue
                .define(
                        this,
                        builder,
                        "Default Amount (joules) of energy Meka-Guns can accept per tick. Quadratically scaled by upgrades.",
                        "chargeRate",
                        FloatingLong.createConst(100_000)
                );
        builder.pop();

        builder.comment("Meka-Rifle Settings").push(MEKARIFLE_CATEGORY);
        mekaRifleBaseEnergyCapability = CachedFloatingLongValue
                .define(
                        this,
                        builder,
                        "Energy capacity (Joules) of the Meka-Rifle without any installed upgrades. Quadratically scaled by upgrades.",
                        "baseEnergyCapability",
                        FloatingLong.createConst(16_000_000)
                );
        mekaRifleBaseChargeRate = CachedFloatingLongValue
                .define(
                        this,
                        builder,
                        "Amount (joules) of energy the Meka-Rifle can accept per tick. Quadratically scaled by upgrades.",
                        "chargeRate",
                        FloatingLong.createConst(100_000));
        builder.pop();

        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "mekagun";
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public ModConfig.Type getConfigType() {
        return ModConfig.Type.SERVER;
    }
}
