package com.teampotato.enchantato_enchantment_table.mixin;

import com.teampotato.enchantato_enchantment_table.EnchantatoEnchantmentTable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentContainerMixin {
    @Redirect(method = "getEnchantmentList", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;selectEnchantment(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;IZ)Ljava/util/List;"))
    private @NotNull List<EnchantmentInstance> onGetEnchantments(RandomSource f, ItemStack list1, int pRandom, boolean pItemStack) {
        return EnchantatoEnchantmentTable.selectEnchantment(f, list1, pRandom, pItemStack);
    }
}
