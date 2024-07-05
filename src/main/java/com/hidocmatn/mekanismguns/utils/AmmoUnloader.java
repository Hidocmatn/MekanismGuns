package com.hidocmatn.mekanismguns.utils;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface AmmoUnloader extends IGun {
    // Unload Ammo Logics
    /**
     * Mostly copied from {@link  com.tacz.guns.network.message.ClientMessageRefitGun} method "dropAllAmmo"
     */
    default void unloadAmmo(Player player, ItemStack gunItem) {
        int ammoCount = getCurrentAmmoCount(gunItem);
        if (ammoCount > 0) {
            ResourceLocation previousGunId = getPreviousGunId(gunItem);
            TimelessAPI.getCommonGunIndex(previousGunId).ifPresent((index) -> {
                ResourceLocation ammoId = index.getGunData().getAmmoId();
                if (player.isCreative()) {
                    int maxAmmCount = AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, index.getGunData());
                    setCurrentAmmoCount(gunItem, maxAmmCount);
                } else {
                    ItemStack barrelBullet = AmmoItemBuilder.create().setId(ammoId).setCount(1).build();
                    if (hasBulletInBarrel(gunItem)) {
                        setBulletInBarrel(gunItem, false);
                        ItemHandlerHelper.giveItemToPlayer(player, barrelBullet);
                    }
                    TimelessAPI.getCommonAmmoIndex(ammoId).ifPresent((ammoIndex) -> {
                        int stackSize = ammoIndex.getStackSize();
                        int tmpAmmoCount = ammoCount;
                        int roundCount = tmpAmmoCount / stackSize;
                        for(int i = 0; i <= roundCount; ++i) {
                            int count = Math.min(tmpAmmoCount, stackSize);
                            ItemStack ammoItem = AmmoItemBuilder.create().setId(ammoId).setCount(count).build();
                            ammoItem.setCount(count);
                            ItemHandlerHelper.giveItemToPlayer(player, ammoItem);
                            tmpAmmoCount -= stackSize;
                        }
                        setCurrentAmmoCount(gunItem, 0);
                    });
                }
            });
        }
        removeUnloadAmmoMark(gunItem);
    }

    /**
     * Get PreviousGunId from gunItem's nbt.
     *
     * @param gunItem
     * @return PreviousGunId as {@link ResourceLocation}.
     */
    default ResourceLocation getPreviousGunId(ItemStack gunItem) {
        CompoundTag nbt = gunItem.getOrCreateTag();
        if (nbt.contains("PreviousGunId", 8)) {
            ResourceLocation gunId = ResourceLocation.tryParse(nbt.getString("PreviousGunId"));
            return Objects.requireNonNullElse(gunId, DefaultAssets.EMPTY_GUN_ID);
        } else {
            //If "PreviousGunId" tag doesn't exist before, initialize it here
            nbt.putString("PreviousGunId", getGunId(gunItem).toString());
            return getGunId(gunItem);
        }
    }

    /**
     * Set gunItem's "PreviousGunId" nbt.
     *
     * @param gunItem
     * @param id      PreviousGunId to Set
     */
    default void setPreviousGunId(ItemStack gunItem, @Nullable ResourceLocation id) {
        CompoundTag nbt = gunItem.getOrCreateTag();
        if (id != null) {
            nbt.putString("PreviousGunId", id.toString());
        }
    }

    /**
     * Write current gunId to "PreviousGunId" nbt.
     */
    default void recordPreviousGunId(ItemStack gunItem) {
        setPreviousGunId(gunItem, getGunId(gunItem));
    }

    /**
     * Mark Itemstack as "ShouldUnloadAmmo".
     */
    default void addUnloadAmmoMark(ItemStack gunItem) {
        CompoundTag nbt =  gunItem.getOrCreateTag();
        nbt.putBoolean("ShouldUnloadAmmo", true);
    }

    /**
     * Remove "ShouldUnloadAmmo" mark from gunItem's nbt.
     */
    default void removeUnloadAmmoMark(ItemStack gunItem) {
        CompoundTag nbt = gunItem.getOrCreateTag();
        nbt.remove("ShouldUnloadAmmo");
    }

    /**
     * Judge whether gunItem should be unloaded.
     */
    default boolean shouldUnloadAmmo(ItemStack gunItem) {
        CompoundTag nbt = gunItem.getOrCreateTag();
        if (nbt.contains("ShouldUnloadAmmo")) {
            return nbt.getBoolean("ShouldUnloadAmmo");
        }
        return false;
    }
}
