package io.github.aelpecyem.la_cucaracha.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import io.github.aelpecyem.la_cucaracha.RoachEntity;

@Environment(EnvType.CLIENT)
public class RoachEntityRenderer extends MobEntityRenderer<RoachEntity, RoachEntityModel> {
	public RoachEntityRenderer(Context context) {
		super(context, new RoachEntityModel(context.getPart(RoachEntityModel.LAYER_LOCATION)), 0.5F);
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
		return LaCucaracha.id("textures/entity/roach.png");
	}
}
