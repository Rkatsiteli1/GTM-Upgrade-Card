package com.gtmplus;

import com.gregtechceu.gtceu.api.GTValues;

import net.minecraft.ChatFormatting;

import javax.annotation.Nullable;

/**
 * The nine upgrade card tiers supported by this mod.
 *
 * <p>Each tier is tied to a GregTech voltage tier (LV = 1 ... UHV = 9) and defines how much
 * extra energy a recipe consumes and how much recipe time is saved.</p>
 */
public enum UpgradeTier {

    LV(1, 2, 0.45f, 0xFFB8B8B8, ChatFormatting.GRAY),
    MV(2, 2, 0.50f, 0xFFC8A100, ChatFormatting.GOLD),
    HV(3, 2, 0.55f, 0xFFE0D000, ChatFormatting.YELLOW),
    EV(4, 3, 0.60f, 0xFF5A5A5A, ChatFormatting.DARK_GRAY),
    IV(5, 3, 0.65f, 0xFF2FA02F, ChatFormatting.DARK_GREEN),
    LUV(6, 3, 0.70f, 0xFFD070D0, ChatFormatting.LIGHT_PURPLE),
    ZPM(7, 4, 0.75f, 0xFFA030A0, ChatFormatting.DARK_PURPLE),
    UV(8, 4, 0.80f, 0xFF3070D0, ChatFormatting.BLUE),
    UHV(9, 4, 0.85f, 0xFFD03030, ChatFormatting.RED);

    private final int tier;
    private final int powerMultiplier;
    private final float timeReduction;
    private final int color;
    private final ChatFormatting formatting;

    UpgradeTier(int tier, int powerMultiplier, float timeReduction, int color, ChatFormatting formatting) {
        this.tier = tier;
        this.powerMultiplier = powerMultiplier;
        this.timeReduction = timeReduction;
        this.color = color;
        this.formatting = formatting;
    }

    /** GT voltage tier index (LV = 1). */
    public int tier() {
        return tier;
    }

    /** Energy consumption multiplier applied to the recipe's EU/t. */
    public int powerMultiplier() {
        return powerMultiplier;
    }

    /** Fraction of recipe duration removed, e.g. 0.45 for a 45% reduction. */
    public float timeReduction() {
        return timeReduction;
    }

    public int color() {
        return color;
    }

    public ChatFormatting formatting() {
        return formatting;
    }

    public long voltage() {
        return GTValues.V[tier];
    }

    /** The id fragment, e.g. {@code lv} or {@code uhv}. */
    public String id() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }

    public String cardId() {
        return id() + "_upgrade_card";
    }

    public String translationKey() {
        return "item.gtmplus." + cardId();
    }

    public String machineTierTranslationKey() {
        return "gtmplus.tier." + id();
    }

    /** Returns the tier matching a GT tier index, or {@code null} if unsupported. */
    @Nullable
    public static UpgradeTier byTier(int tier) {
        for (UpgradeTier value : values()) {
            if (value.tier == tier) return value;
        }
        return null;
    }
}
