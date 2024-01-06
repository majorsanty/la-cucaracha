package io.github.aelpecyem.la_cucaracha.common.mixin;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.BrewingStandScreenHandler$PotionSlot")
public class BrewingStandScreenHandlerPotionSlotMixin {
	@Inject(method = "matches", at = @At("HEAD"), cancellable = true)
	private static void cucaracha_matches(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.isOf(LaCucaracha.BOTTLED_ROACH_ITEM)) {
			cir.setReturnValue(true);
		}
	}
}
