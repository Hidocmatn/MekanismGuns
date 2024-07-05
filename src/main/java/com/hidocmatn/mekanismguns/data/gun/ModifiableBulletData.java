package com.hidocmatn.mekanismguns.data.gun;

import com.tacz.guns.resource.pojo.data.gun.BulletData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModifiableBulletData extends BulletData {
    //Very dirty, but I have no choice.
    private float lifeSecond = 10.0F;
    private int bulletAmount = 1;
    private float damageAmount = 5.0F;
    private @Nullable ModifiableExtraDamage extraDamage;
    private float speed = 5.0F;
    private float gravity = 0.0F;
    private float knockback = 0.0F;
    private float friction = 0.01F;
    private int pierce = 1;
    private boolean hasIgnite = false;
    private int igniteEntityTime = 2;
    private int tracerCountInterval = -1;
    private @Nullable ModifiableExplosionData explosionData;

    public ModifiableBulletData(@Nonnull BulletData data) {
        this.lifeSecond = data.getLifeSecond();
        this.bulletAmount = data.getBulletAmount();
        this.damageAmount = data.getDamageAmount();
        if (data.getExtraDamage() != null) {
            this.extraDamage = new ModifiableExtraDamage(data.getExtraDamage());
        }
        this.speed = data.getSpeed();
        this.gravity = data.getGravity();
        this.gravity = data.getGravity();
        this.knockback = data.getKnockback();
        this.friction = data.getFriction();
        this.pierce = data.getPierce();
        this.hasIgnite = data.isHasIgnite();
        this.igniteEntityTime = data.getIgniteEntityTime();
        this.tracerCountInterval = data.getTracerCountInterval();
        if (data.getExplosionData() != null) {
            this.explosionData = new ModifiableExplosionData(data.getExplosionData());
        }
    }

    @Override
    public float getLifeSecond() {
        return lifeSecond;
    }

    public ModifiableBulletData setLifeSecond(float lifeSecond) {
        this.lifeSecond = lifeSecond;
        return this;
    }

    @Override
    public int getBulletAmount() {
        return bulletAmount;
    }

    public ModifiableBulletData setBulletAmount(int bulletAmount) {
        this.bulletAmount = bulletAmount;
        return this;
    }

    @Override
    public float getDamageAmount() {
        return this.damageAmount;
    }

    public ModifiableBulletData setDamageAmount(float damageAmount) {
        this.damageAmount = damageAmount;
        return this;
    }

    @Override
    @Nullable
    public ModifiableExtraDamage getExtraDamage() {
        return this.extraDamage;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    public ModifiableBulletData setSpeed(float speed) {
        this.speed = speed;
        super.getSpeed();
        return this;
    }

    @Override
    public float getGravity() {
        return gravity;
    }

    public ModifiableBulletData setGravity(float gravity) {
        this.gravity = gravity;
        return this;
    }

    @Override
    public float getKnockback() {
        return knockback;
    }

    public ModifiableBulletData setKnockback(float knockback) {
        this.knockback = knockback;
        return this;
    }

    @Override
    public float getFriction() {
        return friction;
    }

    public ModifiableBulletData setFriction(float friction) {
        this.friction = friction;
        return this;
    }

    @Override
    public int getPierce() {
        return pierce;
    }

    public ModifiableBulletData setPierce(int pierce) {
        this.pierce = pierce;
        return this;
    }

    @Override
    public boolean isHasIgnite() {
        return hasIgnite;
    }

    public ModifiableBulletData setHasIgnite(boolean hasIgnite) {
        this.hasIgnite = hasIgnite;
        return this;
    }

    @Override
    public int getIgniteEntityTime() {
        return igniteEntityTime;
    }

    public ModifiableBulletData setIgniteEntityTime(int igniteEntityTime) {
        this.igniteEntityTime = igniteEntityTime;
        return this;
    }

    @Override
    public int getTracerCountInterval() {
        return tracerCountInterval;
    }

    public ModifiableBulletData setTracerCountInterval(int tracerCountInterval) {
        this.tracerCountInterval = tracerCountInterval;
        return this;
    }

    @Override
    @Nullable
    public ModifiableExplosionData getExplosionData() {
        return explosionData;
    }
}
