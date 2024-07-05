package com.hidocmatn.mekanismguns.item.gun;

import com.hidocmatn.mekanismguns.config.MekanismGunsConfig;
import com.hidocmatn.mekanismguns.data.gun.ModifiableBulletData;
import com.hidocmatn.mekanismguns.init.MekanismGunsModules;
import com.hidocmatn.mekanismguns.item.module.shared.ModuleCoilAccelerator;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.IModule;
import mekanism.api.math.FloatingLong;
import mekanism.common.util.StorageUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Supplier;

public class MekaRifleGunItem extends AbstractMekaGunItem{
    public static final String TYPE_NAME = "meka_rifle";
    public MekaRifleGunItem(Properties properties) {
        super(properties);
    }

    @Override
    public void shoot(ItemStack gunItem, Supplier<Float> pitch, Supplier<Float> yaw, boolean tracer, LivingEntity shooter) {
        ResourceLocation gunId = this.getGunId(gunItem);
        Optional<CommonGunIndex> gunIndexOptional = TimelessAPI.getCommonGunIndex(gunId);
        if (!gunIndexOptional.isEmpty()) {
            FloatingLong shootingEnergyCost = MekanismGunsConfig.MEKA_GUN.mekaGunDefaultEnergyUsageTrigger.get();
            CommonGunIndex gunIndex = gunIndexOptional.get();
            TriggerTask triggerTask = triggerFire(gunItem, shooter, gunIndex);
            ModifiableBulletData bulletData = triggerTask.getBulletData();
            //Coil Boost
            IModule<ModuleCoilAccelerator> coilAcceleratorUnit = getModule(gunItem, MekanismGunsModules.COIL_ACCELERATOR_UNIT);
            if (coilAcceleratorUnit != null && coilAcceleratorUnit.isEnabled()) {
                float damageBoost = coilAcceleratorUnit.getCustomInstance().getDamageBoost();
                bulletData.setDamageAmount(bulletData.getDamageAmount() + damageBoost);
                bulletData.getExtraDamage().replaceAllDamageAdjust(pair -> new ExtraDamage.DistanceDamagePair(pair.getDistance() + (float) Math.log1p(damageBoost), pair.getDamage() + damageBoost * (float) Math.pow(0.99F, pair.getDistance() / 10.0F)));
                FloatingLong coilEnergyCost = MekanismGunsConfig.MEKA_GUN.mekaGunDefaultEnergyUsageCoil.get();
                shootingEnergyCost = shootingEnergyCost.add(coilEnergyCost.multiply(damageBoost));
                triggerTask.setSpeed(triggerTask.getSpeed() + 20.0F * (float) Math.sqrt(damageBoost));
            }
            IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(gunItem, 0);
            //We cannot fire without enough energy!
            if (
                    energyContainer != null
                            && !energyContainer.isEmpty()
                            && energyContainer.getEnergy().greaterOrEqual(shootingEnergyCost)
            ) {
                energyContainer.extract(shootingEnergyCost, Action.EXECUTE, AutomationType.MANUAL);
                //Finally, fire!
                triggerTask.setPitch(pitch.get()).setYaw(yaw.get()).setTracer(tracer).execute();
            }
        }
    }

//    @Override
//    public void reloadAmmo(ItemStack gunItem, int ammoCount, boolean loadBarrel) {
//    }
}
