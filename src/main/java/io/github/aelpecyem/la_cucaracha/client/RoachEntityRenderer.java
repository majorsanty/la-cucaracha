package io.github.aelpecyem.la_cucaracha.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import io.github.aelpecyem.la_cucaracha.RoachEntity;

@Environment(EnvType.CLIENT)
public class RoachEntityRenderer extends MobEntityRenderer<RoachEntity, RoachEntityModel> {
	private final Identifier[] TEXTURE_VARIANTS = {
		LaCucaracha.id("textures/entity/roach.png"),
		LaCucaracha.id("textures/entity/roach_2.png"),
		LaCucaracha.id("textures/entity/roach.png"),
		LaCucaracha.id("textures/entity/roach_2.png")
	};

	public RoachEntityRenderer(Context context) {
		super(context, new RoachEntityModel(context.getPart(RoachEntityModel.LAYER_LOCATION)), 0.5F);
		addFeature(new RoachHeldItemFeatureRenderer(this, context.getHeldItemRenderer()));
	}

	@Override
	public void render(RoachEntity mobEntity, float f, float g, MatrixStack matrixStack,
					   VertexConsumerProvider vertexConsumerProvider, int i) {
		super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Override
	protected void setupTransforms(RoachEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
		super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
		if (entity.isRoachClimbing()) {
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90F));
			matrices.translate(0, -0.1, -0.1);
		}
		float size = 0.25F + entity.getSize() / 10F;
		shadowRadius = size / 4;
		matrices.scale(size, size, size);
	}

	@Override
	public Identifier getTexture(RoachEntity entity) {
		return TEXTURE_VARIANTS[entity.getVariant()];
	}

	public static class RoachHeldItemFeatureRenderer extends FeatureRenderer<RoachEntity, RoachEntityModel> {

		private final HeldItemRenderer heldItemRenderer;

		public RoachHeldItemFeatureRenderer(FeatureRendererContext<RoachEntity, RoachEntityModel> featureRendererContext,
											HeldItemRenderer heldItemRenderer) {
			super(featureRendererContext);
			this.heldItemRenderer = heldItemRenderer;
		}


		@Override
		public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, RoachEntity entity, float limbAngle,
						   float limbDistance,
						   float tickDelta, float animationProgress, float headYaw, float headPitch) {
			ItemStack stack = entity.getMainHandStack();
			if (!stack.isEmpty()) {
				matrices.push();
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				matrices.translate(0, 0.2, -1.45);
				this.heldItemRenderer.renderItem(entity, stack, Mode.GROUND, false, matrices, vertexConsumers, light);
				matrices.pop();
			}
		}
	}
}

