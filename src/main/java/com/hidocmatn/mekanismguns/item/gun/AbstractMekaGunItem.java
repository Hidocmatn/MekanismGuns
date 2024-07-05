package com.hidocmatn.mekanismguns.item.gun;

import com.hidocmatn.mekanismguns.config.MekanismGunsConfig;
import com.hidocmatn.mekanismguns.data.gun.ModifiableBulletData;
import com.hidocmatn.mekanismguns.utils.AmmoUnloader;
import com.hidocmatn.mekanismguns.utils.ShootTask;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.api.item.nbt.GunItemDataAccessor;
import com.tacz.guns.config.common.GunConfig;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.attachment.AttachmentData;
import com.tacz.guns.resource.pojo.data.attachment.Silence;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import com.tacz.guns.sound.SoundManager;
import com.tacz.guns.util.AttachmentDataUtils;
import com.tacz.guns.util.CycleTaskHelper;
import mekanism.api.gear.IModule;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.shared.ModuleEnergyUnit;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.registries.MekanismModules;
import mekanism.common.util.StorageUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractMekaGunItem extends AbstractGunItem implements IModuleContainerItem, IModeItem, GunItemDataAccessor, AmmoUnloader {
    protected AbstractMekaGunItem(Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }

    //MekaTool-Like methods
    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        return StorageUtils.getEnergyBarWidth(itemStack);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        return MekanismConfig.client.energyColor.get();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack itemStack, CompoundTag nbt) {
        return new ItemCapabilityWrapper(itemStack, RateLimitEnergyHandler.create(() -> getChargeRate(itemStack), () -> getMaxEnergy(itemStack), BasicEnergyContainer.manualOnly,
                BasicEnergyContainer.alwaysTrue));
    }

    protected FloatingLong getMaxEnergy(ItemStack itemStack) {
        IModule<ModuleEnergyUnit> module = getModule(itemStack, MekanismModules.ENERGY_UNIT);
        return (module == null) ? (MekanismGunsConfig.MEKA_GUN.mekaGunDefaultBaseEnergyCapability.get()) : (module.getCustomInstance().getEnergyCapacity(module));
    }

    protected FloatingLong getChargeRate(ItemStack itemStack) {
        IModule<ModuleEnergyUnit> module = getModule(itemStack, MekanismModules.ENERGY_UNIT);
        return (module == null) ? (MekanismGunsConfig.MEKA_GUN.mekaGunDefaultBaseChargeRate.get()) : (module.getCustomInstance().getChargeRate(module));
    }

    @Override
    public void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        for (Module<?> module : getModules(stack)) {
            if (module.handlesModeChange()) {
                module.changeMode(player, stack, shift, displayChange);
                return;
            }
        }
    }

    //Gun Methods
    @Override
    public void bolt(ItemStack gunItem) {
        if (this.getCurrentAmmoCount(gunItem) > 0) {
            this.reduceCurrentAmmoCount(gunItem);
            this.setBulletInBarrel(gunItem, true);
        }
    }

    @Override
    public void reloadAmmo(ItemStack gunItem, int ammoCount, boolean loadBarrel) {
        ResourceLocation gunId = this.getGunId(gunItem);
        recordPreviousGunId(gunItem);
        Bolt boltType = TimelessAPI.getCommonGunIndex(gunId).map((index) -> index.getGunData().getBolt()).orElse(null);
        this.setCurrentAmmoCount(gunItem, ammoCount);
        if (loadBarrel && (boltType == Bolt.MANUAL_ACTION || boltType == Bolt.CLOSED_BOLT)) {
            this.reduceCurrentAmmoCount(gunItem);
            this.setBulletInBarrel(gunItem, true);
        }
    }

    //Shooting-related logics
    /**
     * Generate one new TriggerTask with 3 parameters.
     * @param gunItem used for calculation of attachments' influence on shooting sound and inaccuracy, and task creation.
     * @param shooter used for inaccuracy calculation and task creation.
     * @param gunIndex used to get inaccuracy, boltType, ammoId, task interval and cycles from {@link  com.tacz.guns.resource.pojo.data.gun.GunData}.
     * @return one new {@link TriggerTask} with some presets
     * except {@link TriggerTask#pitch},
     * {@link TriggerTask#yaw}
     * and {@link TriggerTask#tracer}.
     */
    protected TriggerTask triggerFire(ItemStack gunItem, LivingEntity shooter, CommonGunIndex gunIndex) {
        //Calculate Attachments' influence
        InaccuracyType inaccuracyState = InaccuracyType.getInaccuracyType(shooter);
        float[] inaccuracy = new float[]{gunIndex.getGunData().getInaccuracy(inaccuracyState)};
        int[] soundDistance = new int[]{GunConfig.DEFAULT_GUN_FIRE_SOUND_DISTANCE.get()};
        boolean[] useSilenceSound = new boolean[]{false};
        AttachmentDataUtils.getAllAttachmentData(gunItem, gunIndex.getGunData(), (attachmentData) -> {
            this.calculateAttachmentData(attachmentData, inaccuracyState, inaccuracy, soundDistance, useSilenceSound);
        });
        inaccuracy[0] = Math.max(0.0F, inaccuracy[0]);
        //Bolt and BulletData
        Bolt boltType = gunIndex.getGunData().getBolt();
        ModifiableBulletData bulletData = new ModifiableBulletData(gunIndex.getBulletData());
        ResourceLocation ammoId = gunIndex.getGunData().getAmmoId();
        //Set task cycles and period
        FireMode fireMode = this.getFireMode(gunItem);
        int cycles = fireMode == FireMode.BURST ? gunIndex.getGunData().getBurstData().getCount() : 1;
        long period = fireMode == FireMode.BURST ? gunIndex.getGunData().getBurstShootInterval() : 1L;
        //Create task, finally
        TriggerTask task = createTask(shooter, gunItem, bulletData)
                .setAmmoId(ammoId)
                .setInaccuracy(inaccuracy[0])
                .setSoundDistance(soundDistance[0])
                .setUseSilenceSound(useSilenceSound[0])
                .setBoltType(boltType)
                .setCycles(cycles)
                .setPeriod(period);
        return task;
    }

    protected void reduceAmmo(ItemStack currentGunItem) {
        Bolt boltType = (Bolt)TimelessAPI.getCommonGunIndex(this.getGunId(currentGunItem)).map((index) -> index.getGunData().getBolt()).orElse(null);
        if (boltType != null) {
            if (boltType == Bolt.MANUAL_ACTION) {
                this.setBulletInBarrel(currentGunItem, false);
            } else if (boltType == Bolt.CLOSED_BOLT) {
                if (this.getCurrentAmmoCount(currentGunItem) > 0) {
                    this.reduceCurrentAmmoCount(currentGunItem);
                } else {
                    this.setBulletInBarrel(currentGunItem, false);
                }
            } else {
                this.reduceCurrentAmmoCount(currentGunItem);
            }
        }
    }

    protected void doSpawnBulletEntity(Level world, LivingEntity shooter, float pitch, float yaw, float speed, float inaccuracy, ResourceLocation ammoId, ResourceLocation gunId, boolean tracer, BulletData bulletData) {
        EntityKineticBullet bullet = new EntityKineticBullet(world, shooter, ammoId, gunId, tracer, bulletData);
        bullet.shootFromRotation(bullet, pitch, yaw, 0.0F, speed, inaccuracy);
        world.addFreshEntity(bullet);
    }

    @Override
    public void fireSelect(ItemStack gunItem) {
        ResourceLocation gunId = this.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).map((gunIndex) -> {
            FireMode fireMode = this.getFireMode(gunItem);
            List<FireMode> fireModeSet = gunIndex.getGunData().getFireModeSet();
            int nextIndex = (fireModeSet.indexOf(fireMode) + 1) % fireModeSet.size();
            FireMode nextFireMode = (FireMode)fireModeSet.get(nextIndex);
            this.setFireMode(gunItem, nextFireMode);
            return nextFireMode;
        });
    }

    public void initFireSelect(ItemStack gunItem) {
        ResourceLocation gunId = this.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).map((gunIndex) -> {
            List<FireMode> fireModeSet = gunIndex.getGunData().getFireModeSet();
            FireMode initFireMode = fireModeSet.get(0);
            this.setFireMode(gunItem, initFireMode);
            return initFireMode;
        });
    }

    //Not needed now, maybe it will be added in the future;
    @Override
    public int getExp(int level) {
        return 0;
    }
    @Override
    public int getMaxLevel() {
        return 0;
    }
    @Override
    public int getLevel(int exp) {
        return 0;
    }

    protected void calculateAttachmentData(AttachmentData attachmentData, InaccuracyType inaccuracyState, float[] inaccuracy, int[] soundDistance, boolean[] useSilenceSound) {
        if (!inaccuracyState.isAim()) {
            inaccuracy[0] += attachmentData.getInaccuracyAddend();
        }
        Silence silence = attachmentData.getSilence();
        if (silence != null) {
            soundDistance[0] += silence.getDistanceAddend();
            if (silence.isUseSilenceSound()) {
                useSilenceSound[0] = true;
            }
        }
    }

    protected TriggerTask createTask(LivingEntity shooter, ItemStack gunItem, ModifiableBulletData bulletData) {
        return new TriggerTask(shooter, gunItem, bulletData);
    }

    protected class TriggerTask implements ShootTask<TriggerTask> {
        private final LivingEntity shooter;
        private final ItemStack gunItem;
        private final ResourceLocation gunId;
        private ResourceLocation ammoId;
        private float pitch;
        private float yaw;
        private float speed;
        private float inaccuracy;
        private int soundDistance;
        private boolean useSilenceSound;
        private boolean consumeAmmo;
        private Bolt boltType;
        private int bulletAmount;
        private boolean tracer;
        private ModifiableBulletData bulletData;
        private long period;
        private int cycles;

        private TriggerTask(LivingEntity entity, ItemStack itemStack, ModifiableBulletData data) {
            this.shooter = entity;
            this.gunItem = itemStack;
            this.gunId = getGunId(gunItem);
            this.ammoId = DefaultAssets.DEFAULT_AMMO_ID;
            initGunData(data);
            initDynamicParameters();
            initSoundSettings();
            this.period = 1L;
            this.cycles = 1;
        }

        private void initGunData(ModifiableBulletData bulletData) {
            this.bulletData = bulletData;
            this.consumeAmmo = IGunOperator.fromLivingEntity(shooter).consumesAmmoOrNot();
            this.boltType = Bolt.CLOSED_BOLT;
            this.bulletAmount = Math.max(bulletData.getBulletAmount(), 1);
            this.tracer = true;
        }

        private void initDynamicParameters() {
            this.pitch = 0.0F;
            this.yaw = 0.0F;
            this.speed = Mth.clamp(bulletData.getSpeed() / 20.0F, 0.0F, Float.MAX_VALUE);
            this.inaccuracy = 0.0F;
        }

        private void initSoundSettings() {
            this.useSilenceSound = false;
            this.soundDistance = (Integer) GunConfig.DEFAULT_GUN_FIRE_SOUND_DISTANCE.get();
        }

        //Basic parts
        @Override
        public LivingEntity getShooter() {
            return shooter;
        }

        @Override
        public ItemStack getGunItem() {
            return gunItem;
        }

        @Override
        public ResourceLocation getAmmoId() {
            return ammoId;
        }

        @Override
        public TriggerTask setAmmoId(ResourceLocation ammoId) {
            this.ammoId = ammoId;
            return this;
        }

        @Override
        public ResourceLocation getShootingGunId() {
            return gunId;
        }

        //Dynamics
        @Override
        public float getPitch() {
            return pitch;
        }

        @Override
        public TriggerTask setPitch(Float pitch) {
            this.pitch = pitch;
            return this;
        }

        @Override
        public float getYaw() {
            return yaw;
        }

        @Override
        public TriggerTask setYaw(Float yaw) {
            this.yaw = yaw;
            return this;
        }

        @Override
        public float getSpeed() {
            return speed;
        }

        @Override
        public TriggerTask setSpeed(float speed) {
            this.speed = speed;
            return this;
        }

        @Override
        public float getInaccuracy() {
            return inaccuracy;
        }

        @Override
        public TriggerTask setInaccuracy(float inaccuracy) {
            this.inaccuracy = inaccuracy;
            return this;
        }

        //Sound
        @Override
        public int getSoundDistance() {
            return soundDistance;
        }

        @Override
        public TriggerTask setSoundDistance(int soundDistance) {
            this.soundDistance = soundDistance;
            return this;
        }

        @Override
        public boolean isUseSilenceSound() {
            return useSilenceSound;
        }

        @Override
        public TriggerTask setUseSilenceSound(boolean useSilenceSound) {
            this.useSilenceSound = useSilenceSound;
            return this;
        }

        //Ammo and bolt
        @Override
        public boolean isConsumeAmmo() {
            return consumeAmmo;
        }

        @Override
        public TriggerTask consumeAmmo(boolean consumeAmmo) {
            this.consumeAmmo = consumeAmmo;
            return this;
        }

        @Override
        public Bolt getBoltType() {
            return boltType;
        }

        @Override
        public TriggerTask setBoltType(Bolt boltType) {
            this.boltType = boltType;
            return this;
        }

        @Override
        public int getBulletAmount() {
            return bulletAmount;
        }

        @Override
        public TriggerTask setBulletAmount(int bulletAmount) {
            this.bulletAmount = bulletAmount;
            return this;
        }

        @Override
        public boolean hasTracer() {
            return tracer;
        }

        @Override
        public TriggerTask setTracer(boolean tracer) {
            this.tracer = tracer;
            return this;
        }

        @Override
        public ModifiableBulletData getBulletData() {
            return bulletData;
        }

        //Task Period and Cycles
        @Override
        public long getPeriod() {
            return period;
        }

        @Override
        public TriggerTask setPeriod(long period) {
            this.period = period;
            return this;
        }

        @Override
        public int getCycles() {
            return cycles;
        }

        @Override
        public TriggerTask setCycles(int cycles) {
            this.cycles = cycles;
            return this;
        }

        public boolean continueConsumption() {
            if (isConsumeAmmo()) {
                boolean hasAmmoInBarrel = hasBulletInBarrel(getGunItem()) && getBoltType() != Bolt.OPEN_BOLT;
                int i = getCurrentAmmoCount(getGunItem()) + (hasAmmoInBarrel ? 1 : 0);
                if (i <= 0) {
                    return false;
                }
            }
            return true;
        }

        public void spawnBullet() {
            if (isConsumeAmmo()) {
                reduceAmmo(getGunItem());
            }
            Level world = getShooter().level();
            for(int i = 0; i < getBulletAmount(); i++) {
                doSpawnBulletEntity(world, getShooter(), getPitch(), getYaw(), getSpeed(), getInaccuracy(), getAmmoId(), getShootingGunId(), hasTracer(), getBulletData());
            }
        }

        public void playSound() {
            if (getSoundDistance() > 0) {
                String soundId = isUseSilenceSound() ? SoundManager.SILENCE_3P_SOUND : SoundManager.SHOOT_3P_SOUND;
                SoundManager.sendSoundToNearby(getShooter(), getSoundDistance(), getShootingGunId(), soundId, 0.8F, 0.9F + getShooter().getRandom().nextFloat() * 0.125F);
            }
        }

        @Override
        public void execute() {
            CycleTaskHelper.addCycleTask(()-> {
                if (!continueConsumption()) {
                    return false;
                }
                if (!MinecraftForge.EVENT_BUS.post(new GunFireEvent(getShooter(), getGunItem(), LogicalSide.SERVER))) {
                    spawnBullet();
                    playSound();
                }
                return true;
            }, getPeriod(), getCycles());
        }
    }
}
