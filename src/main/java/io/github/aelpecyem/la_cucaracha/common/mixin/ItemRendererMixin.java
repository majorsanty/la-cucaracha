package io.github.aelpecyem.la_cucaracha.common.mixin;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.render.item.ItemRenderer.getDirectItemGlintConsumer;

@Mixin(ItemRenderer.class)
abstract class ItemRendererMixin {
	@Shadow
	@Final
	private ItemModels models;

	@Shadow
	@Final
	private BuiltinModelItemRenderer builtinModelItemRenderer;

	@Shadow
	protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices);
	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
		at = @At("HEAD"), cancellable = true)
	public void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
		if (stack.isEmpty())
			return;

		// yoink from tona
		if(stack.isOf(LaCucaracha.CAUSTIC_CLAW_ITEM)
			&& (renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED)) {
			matrices.push();

			Identifier loc = Registries.ITEM.getId(stack.getItem()).withSuffixedPath("_in_inventory");
			model = this.models.getModelManager().getModel(new ModelIdentifier(loc, "inventory"));
			model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
			matrices.translate(-0.5, -0.5, -0.5);
			if (!model.isBuiltin()) {
				VertexConsumer vertexConsumer = getDirectItemGlintConsumer(vertexConsumers,
					RenderLayers.getItemLayer(stack, true),
					true,
					false);
				this.renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);
			} else
				this.builtinModelItemRenderer.render(stack, renderMode, matrices, vertexConsumers, light, overlay);

			matrices.pop();
			ci.cancel();
		}
	}

}
