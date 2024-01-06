package io.github.aelpecyem.la_cucaracha.common.mixin;

import net.minecraft.entity.ai.brain.task.FrogEatEntityTask;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FrogEatEntityTask.class)
public class CatchAndEatEntityTaskMixin {

	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;remove(Lnet/minecraft/entity/Entity$RemovalReason;)V"))
	private void cucaracha_spawnSlime(ServerWorld world, FrogEntity frog, CallbackInfo ci) {
		frog.dropItem(Items.SLIME_BALL);
	}
}
