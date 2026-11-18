package com.gtmplus;

import com.gtmplus.registry.ModCreativeTabs;
import com.gtmplus.registry.ModItems;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * GTM Plus - GregTech CEu Modern addon.
 *
 * <p>Adds a single voltage-tiered upgrade card slot to GT machines and multiblock controllers.
 * Installing the card matching the machine's voltage tier increases recipe speed at the cost of
 * additional energy consumption.</p>
 */
@Mod(GTMPlus.MOD_ID)
public class GTMPlus {

    public static final String MOD_ID = "gtmplus";

    public GTMPlus() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
    }
}
