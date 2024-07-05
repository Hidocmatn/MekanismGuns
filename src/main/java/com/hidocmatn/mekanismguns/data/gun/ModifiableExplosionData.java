package com.hidocmatn.mekanismguns.data.gun;

import com.tacz.guns.resource.pojo.data.gun.ExplosionData;

import javax.annotation.Nonnull;

public class ModifiableExplosionData extends ExplosionData {
    //Very dirty, but I have no choice.
    private float radius = 5.0F;
    private float damage = 5.0F;
    private boolean knockback = false;

    public ModifiableExplosionData(@Nonnull ExplosionData data) {
        this.radius = data.getRadius();
        this.damage = data.getDamage();
        this.knockback = data.isKnockback();
    }

    @Override
    public float getRadius() {
        return radius;
    }

    public ModifiableExplosionData setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    public ModifiableExplosionData setDamage(float damage) {
        this.damage = damage;
        return this;
    }

    @Override
    public boolean isKnockback() {
        return knockback;
    }

    public ModifiableExplosionData setKnockback(boolean knockback) {
        this.knockback = knockback;
        return this;
    }
}
