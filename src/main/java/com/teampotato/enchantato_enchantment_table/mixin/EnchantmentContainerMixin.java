package com.teampotato.enchantato_enchantment_table.mixin;

import com.teampotato.enchantato_enchantment_table.EnchantatoEnchantmentTable;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Random;

@Mixin(EnchantmentContainer.class)
public abstract class EnchantmentContainerMixin {
    @Redirect(method = "getEnchantmentList", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;selectEnchantment(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Ljava/util/List;"))
    private @NotNull List<EnchantmentData> onGetEnchantments(Random f, ItemStack list1, int pRandom, boolean pItemStack) {
        return EnchantatoEnchantmentTable.selectEnchantment(f, list1, pRandom, pItemStack);
    }
}
