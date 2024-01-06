package io.github.aelpecyem.la_cucaracha.client;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import io.github.aelpecyem.la_cucaracha.common.entity.LaCucarachaEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class LaCucarachaEntityRenderer extends MobEntityRenderer<LaCucarachaEntity, LaCucarachaEntityModel> {
	private final Identifier[] TEXTURE_VARIANTS = {
		LaCucaracha.id("textures/entity/la_cucaracha.png")
	};

	public LaCucarachaEntityRenderer(Context context) {
		super(context, new LaCucarachaEntityModel(context.getPart(LaCucarachaEntityModel.LAYER_LOCATION)), 0.5F);
		addFeature(new RoachHeldItemFeatureRenderer(this, context.getHeldItemRenderer()));
	}

	@Override
	public void render(LaCucarachaEntity mobEntity, float f, float g, MatrixStack matrixStack,
					   VertexConsumerProvider vertexConsumerProvider, int i) {
		super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Override
	protected void setupTransforms(LaCucarachaEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
		super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
		if (entity.isRoachClimbing()) {
			// #TODO he's a very big boy, depending on how much he gotta climb, change degrees
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(60F));
			matrices.translate(0, -0.1, -0.1);
		}
		float size = 1F + entity.getSize() / 10F;
		shadowRadius = size / 4;
		matrices.scale(size, size, size);
	}

	@Override
	public Identifier getTexture(LaCucarachaEntity entity) {
		return TEXTURE_VARIANTS[0];
	}

	public static class RoachHeldItemFeatureRenderer extends FeatureRenderer<LaCucarachaEntity, LaCucarachaEntityModel> {

		private final HeldItemRenderer heldItemRenderer;

		public RoachHeldItemFeatureRenderer(FeatureRendererContext<LaCucarachaEntity, LaCucarachaEntityModel> featureRendererContext,
											HeldItemRenderer heldItemRenderer) {
			super(featureRendererContext);
			this.heldItemRenderer = heldItemRenderer;
		}


		@Override
		public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LaCucarachaEntity entity, float limbAngle,
						   float limbDistance,
						   float tickDelta, float animationProgress, float headYaw, float headPitch) {
			ItemStack stack = entity.getMainHandStack();
			if (!stack.isEmpty()) {
				matrices.push();
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
				matrices.translate(0, -1.5, -0.7);
				this.heldItemRenderer.renderItem(entity, stack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light);

				matrices.pop();
			}
		}
	}
}

