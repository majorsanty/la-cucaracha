package io.github.aelpecyem.la_cucaracha.client;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import io.github.aelpecyem.la_cucaracha.RoachEntity;

public class RoachEntityModel extends EntityModel<RoachEntity> {
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(LaCucaracha.id("roach"), "main");
	private final ModelPart roach;
	private final ModelPart head;
	private final ModelPart rAntenna;
	private final ModelPart lAntenna;
	private final ModelPart rLeg01, rLeg02, rLeg03;
	private final ModelPart lLeg01, lLeg02, lLeg03;

	public RoachEntityModel(ModelPart root) {
		this.roach = root.getChild("roach");
		ModelPart thorax = roach.getChild("thorax");
		this.head = thorax.getChild("head");
		this.rAntenna = head.getChild("rAntenna");
		this.lAntenna = head.getChild("lAntenna");
		this.rLeg01 = thorax.getChild("rLeg01");
		this.rLeg02 = thorax.getChild("rLeg02");
		this.rLeg03 = thorax.getChild("rLeg03");
		this.lLeg01 = thorax.getChild("lLeg01");
		this.lLeg02 = thorax.getChild("lLeg02");
		this.lLeg03 = thorax.getChild("lLeg03");
	}

	public static TexturedModelData createTexturedModelData() {
		ModelData data = new ModelData();
		ModelPartData root = data.getRoot();

		ModelPartData roach = root.addChild("roach", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 22.0F, 0.0F));

		ModelPartData thorax = roach.addChild("thorax", ModelPartBuilder.create().uv(0, 6).cuboid(-2.0F, -1.5F, -4.0F, 4.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData tail = thorax.addChild("tail", ModelPartBuilder.create().uv(13, 7).cuboid(-2.5F, 0.0F, 0.0F, 5.0F, 0.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 5.0F, 0.3054F, 0.0F, 0.0F));

		ModelPartData head = thorax.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -1.5F, -2.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -4.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData lAntenna = head.addChild("lAntenna", ModelPartBuilder.create().uv(0, 6).cuboid(-0.5F, -10.0F, 0.0F, 5.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -1.5F, 0.75F, 0.6981F, 0.0F, 0.3491F));

		ModelPartData rAntenna = head.addChild("rAntenna", ModelPartBuilder.create().uv(0, 6).mirrored().cuboid(-4.5F, -10.0F, 0.0F, 5.0F, 10.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.0F, -1.5F, 0.75F, 0.6981F, 0.0F, -0.3491F));

		ModelPartData lLeg01 = thorax.addChild("lLeg01", ModelPartBuilder.create().uv(9, 0).cuboid(0.0F, 0.0F, -0.5F, 6.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 0.5F, -2.0F, 0.0F, 0.6545F, 0.3491F));

		ModelPartData rLeg01 = thorax.addChild("rLeg01", ModelPartBuilder.create().uv(9, 0).mirrored().cuboid(-6.0F, 0.0F, -0.5F, 6.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.0F, 0.5F, -2.0F, 0.0F, -0.6545F, -0.3491F));

		ModelPartData lLeg02 = thorax.addChild("lLeg02", ModelPartBuilder.create().uv(9, 2).cuboid(0.0F, 0.0F, -0.5F, 6.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 0.5F, -1.0F, 0.0F, 0.0F, 0.2618F));

		ModelPartData rLeg02 = thorax.addChild("rLeg02", ModelPartBuilder.create().uv(9, 2).mirrored().cuboid(-6.0F, 0.0F, -0.5F, 6.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.0F, 0.5F, -1.0F, 0.0F, 0.0F, -0.2618F));

		ModelPartData lLeg03 = thorax.addChild("lLeg03", ModelPartBuilder.create().uv(6, 21).cuboid(0.0F, 0.0F, -3.5F, 11.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 0.5F, 0.0F, 0.0F, -1.2217F, 0.2182F));

		ModelPartData rLeg03 = thorax.addChild("rLeg03", ModelPartBuilder.create().uv(6, 21).mirrored().cuboid(-11.0F, 0.0F, -3.5F, 11.0F, 0.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.0F, 0.5F, 0.0F, 0.0F, 1.2217F, -0.2182F));

		ModelPartData lWing = thorax.addChild("lWing", ModelPartBuilder.create().uv(-10, 19).cuboid(-2.0F, -0.25F, -0.5F, 4.0F, 0.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -1.5F, -2.0F, 0.0436F, 0.0436F, 0.2618F));

		ModelPartData rWing = thorax.addChild("rWing", ModelPartBuilder.create().uv(-10, 19).mirrored().cuboid(-2.0F, -0.25F, -0.5F, 4.0F, 0.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.0F, -1.5F, -2.0F, 0.0436F, -0.0436F, -0.2182F));

		return TexturedModelData.of(data, 32, 32);
	}

	@Override
	public void setAngles(RoachEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		boolean dancing = false;
		int twitchInterval = 60 + entity.getId() % 20;
		if (animationProgress % twitchInterval < 5) {
			float progress = MathHelper.TAU * (animationProgress % 5) / 5F;
			ModelPart antenna = animationProgress % (twitchInterval * 2) >= twitchInterval ? lAntenna : rAntenna;
			antenna.yaw = MathHelper.PI * 0.125F * MathHelper.sin(progress);
		} else {
			rAntenna.yaw = 0;
			lAntenna.yaw = 0;
		}
		float cycle = (MathHelper.cos(limbAngle * 0.6662F * 2.0F)) * 0.4F * limbDistance;
		rLeg01.yaw = -0.6545F - cycle;
		lLeg01.yaw = 0.6545F - cycle;
		rLeg02.yaw = cycle;
		lLeg02.yaw = cycle;
		lLeg03.yaw = -1.2217F - cycle * 2;
		rLeg03.yaw = 1.2217F - cycle * 2;
		if (dancing) {
			roach.yaw = animationProgress * 0.2F;
			roach.pitch = -MathHelper.PI * 0.25F;
			roach.pivotY = 16 + MathHelper.sin(animationProgress * 0.75F) * 2;
		} else {
			roach.yaw = 0;
			roach.pitch = 0;
			roach.pivotY = 22;
		}
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
					   float red, float green, float blue, float alpha) {
		roach.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
