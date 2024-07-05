package com.hidocmatn.mekanismguns.utils;

import com.hidocmatn.mekanismguns.data.gun.ModifiableBulletData;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ShootTask<T> {
    //Basic parts
    LivingEntity getShooter();

    ItemStack getGunItem();

    ResourceLocation getAmmoId();

    T setAmmoId(ResourceLocation ammoId);

    ResourceLocation getShootingGunId();

    //Dynamics
    float getPitch();

    T setPitch(Float pitch);

    float getYaw();

    T setYaw(Float yaw);

    float getSpeed();

    T setSpeed(float speed);

    float getInaccuracy();

    T setInaccuracy(float inaccuracy);

    //Sound
    int getSoundDistance();

    T setSoundDistance(int soundDistance);

    boolean isUseSilenceSound();

    T setUseSilenceSound(boolean useSilenceSound);

    //Ammo and bolt
    boolean isConsumeAmmo();

    T consumeAmmo(boolean consumeAmmo);

    Bolt getBoltType();

    T setBoltType(Bolt boltType);

    int getBulletAmount();

    T setBulletAmount(int bulletAmount);

    boolean hasTracer();

    T setTracer(boolean tracer);

    ModifiableBulletData getBulletData();

    //Task Period and Cycles
    long getPeriod();

    T setPeriod(long period);

    int getCycles();

    T setCycles(int cycles);

    void execute();
}
