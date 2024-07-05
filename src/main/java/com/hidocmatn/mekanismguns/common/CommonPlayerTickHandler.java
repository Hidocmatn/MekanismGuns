package com.hidocmatn.mekanismguns.common;

import com.hidocmatn.mekanismguns.MekanismGuns;
import com.hidocmatn.mekanismguns.utils.AmmoUnloader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MekanismGuns.MODID)
public class CommonPlayerTickHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isServer()) {
            Player player = event.player;
            if (!player.getInventory().isEmpty()) {
                Inventory inventory = player.getInventory();
                for (ItemStack inventoryItem : inventory.items) {
                    handleUnload(player, inventoryItem);
                }
                for (ItemStack offhandItem : inventory.offhand) {
                    handleUnload(player, offhandItem);
                }
            }
        }
    }

    private static void handleUnload(Player player, ItemStack gunItem) {
        if (gunItem.getItem() instanceof AmmoUnloader unloader) {
            if (unloader.shouldUnloadAmmo(gunItem)) {
                unloader.unloadAmmo(player, gunItem);
            }
        }
    }
}
