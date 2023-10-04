package com.teampotato.enchantato_enchantment_table;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Mod("enchantato_enchantment_table")
@SuppressWarnings("DataFlowIssue")
public class EnchantatoEnchantmentTable {
    public static @NotNull List<EnchantmentInstance> selectEnchantment(RandomSource pRandom, @NotNull ItemStack pItemStack, int pLevel, boolean pAllowTreasure) {
        List<EnchantmentInstance> list = new ObjectArrayList<>();
        int i = pItemStack.getEnchantmentValue();
        if (i > 0) {
            pLevel += 1 + pRandom.nextInt(i / 4 + 1) + pRandom.nextInt(i / 4 + 1);
            float f = (pRandom.nextFloat() + pRandom.nextFloat() - 1.0F) * 0.15F;
            pLevel = Mth.clamp(Math.round((float) pLevel + (float) pLevel * f), 1, Integer.MAX_VALUE);
            List<EnchantmentInstance> list1 = getAvailableEnchantmentResults(pLevel, pItemStack, pAllowTreasure);
            if (!list1.isEmpty()) {
                WeightedRandom.getRandomItem(pRandom, list1).ifPresent(list::add);

                while (pRandom.nextInt(50) <= pLevel) {
                    if (!list.isEmpty()) {
                        EnchantmentHelper.filterCompatibleEnchantments(list1, Util.lastOf(list));
                    }

                    if (list1.isEmpty()) {
                        break;
                    }

                    WeightedRandom.getRandomItem(pRandom, list1).ifPresent(list::add);
                    pLevel /= 2;
                }
            }

        }
        return list;
    }

    public static @NotNull List<EnchantmentInstance> getAvailableEnchantmentResults(int pLevel, @NotNull ItemStack pStack, boolean pAllowTreasure) {
        List<EnchantmentInstance> list = new ObjectArrayList<>();
        boolean flag = pStack.is(Items.BOOK);

        for(Enchantment enchantment : Registry.ENCHANTMENT) {
            if ((!enchantment.isTreasureOnly() || pAllowTreasure) && enchantment.isDiscoverable() && (onGetEnchantments(enchantment, pStack) || (flag && enchantment.isAllowedOnBooks()))) {
                for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (pLevel >= enchantment.getMinCost(i) && pLevel <= enchantment.getMaxCost(i)) {
                        list.add(new EnchantmentInstance(enchantment, i));
                        break;
                    }
                }
            }
        }

        return list;
    }

    private static boolean onGetEnchantments(@NotNull Enchantment instance, ItemStack stack) {
        if (!INVERTED_MODE.get()) {
            if (ENCHANTMENT_LIST.get().contains(ForgeRegistries.ENCHANTMENTS.getKey(instance).toString())) return false;
            return instance.canApplyAtEnchantingTable(stack);
        } else {
            return instance.canApplyAtEnchantingTable(stack) && ENCHANTMENT_LIST.get().contains(ForgeRegistries.ENCHANTMENTS.getKey(instance).toString());
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
