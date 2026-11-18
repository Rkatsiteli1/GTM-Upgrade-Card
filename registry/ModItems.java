package com.gtmplus.registry;

import com.gtmplus.GTMPlus;
import com.gtmplus.UpgradeTier;
import com.gtmplus.item.UpgradeCardItem;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GTMPlus.MOD_ID);

    private static final Map<UpgradeTier, RegistryObject<Item>> UPGRADE_CARDS = new EnumMap<>(UpgradeTier.class);
    public static final List<RegistryObject<Item>> ALL = new ArrayList<>();

    static {
        for (UpgradeTier tier : UpgradeTier.values()) {
            RegistryObject<Item> item = ITEMS.register(tier.cardId(),
                    () -> new UpgradeCardItem(tier, new Item.Properties()));
            UPGRADE_CARDS.put(tier, item);
            ALL.add(item);
        }
    }

    public static RegistryObject<Item> card(UpgradeTier tier) {
        return UPGRADE_CARDS.get(tier);
    }

    public static Map<UpgradeTier, RegistryObject<Item>> upgradeCards() {
        return Collections.unmodifiableMap(UPGRADE_CARDS);
    }

    private ModItems() {}
}
