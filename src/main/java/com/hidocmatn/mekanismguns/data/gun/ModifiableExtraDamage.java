package com.hidocmatn.mekanismguns.data.gun;

import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.function.UnaryOperator;

public class ModifiableExtraDamage extends ExtraDamage {
    //Very dirty, but I have no choice.
    private float armorIgnore;
    private float headShotMultiplier;
    private LinkedList<DistanceDamagePair> damageAdjust;

    public ModifiableExtraDamage(@Nonnull ExtraDamage data) {
        this.armorIgnore = data.getArmorIgnore();
        this.headShotMultiplier = data.getHeadShotMultiplier();
        this.damageAdjust = (LinkedList<DistanceDamagePair>) data.getDamageAdjust().clone();
    }

    @Override
    public float getArmorIgnore() {
        return this.armorIgnore;
    }

    public ModifiableExtraDamage setArmorIgnore(float armorIgnore) {
        this.armorIgnore = armorIgnore;
        return this;
    }

    @Override
    public float getHeadShotMultiplier() {
        return this.headShotMultiplier;
    }

    public ModifiableExtraDamage setHeadShotMultiplier(float headShotMultiplier) {
        this.headShotMultiplier = headShotMultiplier;
        return this;
    }

    @Override
    public LinkedList<DistanceDamagePair> getDamageAdjust() {
        return this.damageAdjust;
    }

    public ModifiableExtraDamage setDamageAdjust(LinkedList<DistanceDamagePair> damageAdjust) {
        this.damageAdjust = damageAdjust;
        return this;
    }

    public ModifiableExtraDamage addDamageAdjust(float add) {
        damageAdjust.replaceAll(pair -> pair = new DistanceDamagePair(pair.getDistance(), pair.getDamage() + add)
        );
        return this;
    }

    public ModifiableExtraDamage multiplyDamageAdjust(float multiply) {
        damageAdjust.replaceAll(pair -> pair = new DistanceDamagePair(pair.getDistance(), pair.getDamage() * multiply));
        return this;
    }

    public ModifiableExtraDamage addDistanceAdjust(float add) {
        damageAdjust.replaceAll(pair -> pair = new DistanceDamagePair(pair.getDistance() + add, pair.getDamage()));
        return this;
    }

    public ModifiableExtraDamage multiplyDistanceAdjust(float multiply) {
        damageAdjust.replaceAll(pair -> pair = new DistanceDamagePair(pair.getDistance() * multiply, pair.getDamage()));
        return this;
    }

    public ModifiableExtraDamage replaceAllDamageAdjust(UnaryOperator<DistanceDamagePair> operator) {
        damageAdjust.replaceAll(operator);
        return this;
    }
}
