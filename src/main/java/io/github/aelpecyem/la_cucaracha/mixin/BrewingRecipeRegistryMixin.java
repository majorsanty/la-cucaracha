package io.github.aelpecyem.la_cucaracha.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

	@Inject(method = "hasRecipe", at = @At("HEAD"), cancellable = true)
	private static void cucaracha_hasRecipe(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
		if (input.isOf(LaCucaracha.BOTTLED_ROACH_ITEM) && ingredient.isOf(Items.GUNPOWDER)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "craft", at = @At("HEAD"), cancellable = true)
	private static void cucaracha_craftSplashRoach(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
		if (input.isOf(LaCucaracha.BOTTLED_ROACH_ITEM) && ingredient.isOf(Items.GUNPOWDER)) {
			cir.setReturnValue(LaCucaracha.SPLASH_POTION_ROACH_ITEM.getDefaultStack());
		}
	}
}
