package com.gtmplus.registry;

import com.gtmplus.GTMPlus;
import com.gtmplus.UpgradeTier;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            GTMPlus.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.gtmplus"))
            .icon(() -> new ItemStack(ModItems.card(UpgradeTier.UHV).get()))
            .displayItems((parameters, output) -> ModItems.ALL.forEach(item -> output.accept(item.get())))
            .build());

    private ModCreativeTabs() {}
}
