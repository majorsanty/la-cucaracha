package io.github.aelpecyem.la_cucaracha.mixin;

import net.minecraft.entity.ai.brain.task.CatchAndEatEntityTask;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatchAndEatEntityTask.class)
public class CatchAndEatEntityTaskMixin {

	@Inject(method = "eatTargetEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;remove(Lnet/minecraft/entity/Entity$RemovalReason;)V"))
	private void cucaracha_spawnSlime(ServerWorld world, FrogEntity frog, CallbackInfo ci) {
		frog.dropItem(Items.SLIME_BALL);
	}
}
