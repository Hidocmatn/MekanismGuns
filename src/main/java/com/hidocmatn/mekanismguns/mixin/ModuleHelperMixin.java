package com.hidocmatn.mekanismguns.mixin;

import com.google.common.collect.ImmutableSet;
import com.hidocmatn.mekanismguns.MekanismGunsIMC;
import com.hidocmatn.mekanismguns.init.MekanismGunsItems;
import mekanism.api.gear.ModuleData;
import mekanism.api.providers.IItemProvider;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.registration.impl.ItemRegistryObject;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.Set;

@Mixin(value = ModuleHelper.class, remap = false)
public abstract class ModuleHelperMixin {
    @Shadow protected abstract void mapSupportedModules(InterModProcessEvent event, String imcMethod, IItemProvider moduleContainer, Map<ModuleData<?>, ImmutableSet.Builder<Item>> supportedContainersBuilderMap);

    @Shadow @Final private Map<ModuleData<?>, Set<Item>> supportedContainers;

    //As Mekanism 10.0.4 doesn't provide register method for module containers, we need mixin to accomplish it.
    //Thanks for Mekanism Weapons for these codes.
    @Inject(
            method = "processIMC",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/content/gear/ModuleHelper;mapSupportedModules(Lnet/minecraftforge/fml/event/lifecycle/InterModProcessEvent;Ljava/lang/String;Lmekanism/api/providers/IItemProvider;Ljava/util/Map;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void processIMCMixin(InterModProcessEvent event, CallbackInfo ci, Map<ModuleData<?>, ImmutableSet.Builder<Item>> supportedContainersBuilderMap) {
        mapSupportedModules(event, MekanismGunsIMC.ADD_MEKARIFLE_MODULES, new ItemRegistryObject(MekanismGunsItems.MEKA_RIFLE), supportedContainersBuilderMap);
    }
}
