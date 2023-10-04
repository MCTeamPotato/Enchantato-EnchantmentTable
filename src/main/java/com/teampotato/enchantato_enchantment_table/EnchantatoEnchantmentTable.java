package com.teampotato.enchantato_enchantment_table;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

@Mod("enchantato_enchantment_table")
@SuppressWarnings("DataFlowIssue")
public class EnchantatoEnchantmentTable {
    public static @NotNull List<EnchantmentData> selectEnchantment(Random pRandom, @NotNull ItemStack pItemStack, int pLevel, boolean pAllowTreasure) {
        List<EnchantmentData> list = new ObjectArrayList<>();
        int i = pItemStack.getItemEnchantability();
        if (i > 0) {
            pLevel = pLevel + 1 + pRandom.nextInt(i / 4 + 1) + pRandom.nextInt(i / 4 + 1);
            float f = (pRandom.nextFloat() + pRandom.nextFloat() - 1.0F) * 0.15F;
            pLevel = MathHelper.clamp(Math.round((float) pLevel + (float) pLevel * f), 1, Integer.MAX_VALUE);
            List<EnchantmentData> availableEnchantmentResults = getAvailableEnchantmentResults(pLevel, pItemStack, pAllowTreasure);
            if (!availableEnchantmentResults.isEmpty()) {
                list.add(WeightedRandom.getRandomItem(pRandom, availableEnchantmentResults));
                while (pRandom.nextInt(50) <= pLevel) {
                    EnchantmentHelper.filterCompatibleEnchantments(availableEnchantmentResults, Util.lastOf(list));
                    if (availableEnchantmentResults.isEmpty()) break;
                    list.add(WeightedRandom.getRandomItem(pRandom, availableEnchantmentResults));
                    pLevel /= 2;
                }
            }
        }
        return list;
    }

    public static @NotNull List<EnchantmentData> getAvailableEnchantmentResults(int pLevel, @NotNull ItemStack pStack, boolean pAllowTreasure) {
        List<EnchantmentData> list = Lists.newArrayList();
        boolean flag = pStack.getItem() == Items.BOOK;

        for(Enchantment enchantment : Registry.ENCHANTMENT) {
            if ((!enchantment.isTreasureOnly() || pAllowTreasure) && enchantment.isDiscoverable() && (onGetEnchantments(enchantment, pStack) || (flag && enchantment.isAllowedOnBooks()))) {
                for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (pLevel >= enchantment.getMinCost(i) && pLevel <= enchantment.getMaxCost(i)) {
                        list.add(new EnchantmentData(enchantment, i));
                        break;
                    }
                }
            }
        }

        return list;
    }

    private static boolean onGetEnchantments(@NotNull Enchantment instance, ItemStack stack) {
        if (!INVERTED_MODE.get()) {
            if (ENCHANTMENT_LIST.get().contains(instance.getRegistryName().toString())) return false;
            return instance.canApplyAtEnchantingTable(stack);
        } else {
            return instance.canApplyAtEnchantingTable(stack) && ENCHANTMENT_LIST.get().contains(instance.getRegistryName().toString());
        }
    }

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ENCHANTMENT_LIST;
    public static ForgeConfigSpec.BooleanValue INVERTED_MODE;

    static {
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
        CONFIG_BUILDER.push("Enchantato-EnchantmentTableUsage");
        INVERTED_MODE = CONFIG_BUILDER.comment("If you enable this, the 'DisabledEnchantments' will become 'AllowedEnchantments'").define("InvertedMode", false);
        ENCHANTMENT_LIST = CONFIG_BUILDER.defineList("DisabledEnchantments", new ObjectArrayList<>(), o -> true);
        CONFIG_BUILDER.pop();
        COMMON_CONFIG = CONFIG_BUILDER.build();
    }

    public EnchantatoEnchantmentTable() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG, "enchantato-enchantmentTableUsage.toml");
    }
}
