package io.github.aelpecyem.la_cucaracha.client;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import io.github.aelpecyem.la_cucaracha.common.entity.LaCucarachaEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 4.9.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class LaCucarachaEntityModel extends EntityModel<LaCucarachaEntity> {
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(LaCucaracha.id("la_cucaracha"), "main");


	private final ModelPart roach;
	private final ModelPart thorax;
	private final ModelPart abdomin;
	// private final ModelPart eggcase;
	private final ModelPart head;
	private final ModelPart upperJaw;
	private final ModelPart lowerJaw;

	private final ModelPart lAntenna;
	private final ModelPart rAntenna;

	private final ModelPart lWing01;
	private final ModelPart rWing01;
	private final ModelPart lWing02;
	private final ModelPart rWing02;
	private final ModelPart sombrero;

	private final ModelPart lLeg01a;
	private final ModelPart rLeg01a;
	private final ModelPart lLeg02a;
	private final ModelPart rLeg02a;
	private final ModelPart lLeg03a;
	private final ModelPart rLeg03a;

	/*
	private final ModelPart cube_r1;
	private final ModelPart cube_r2;
	private final ModelPart lLeg01b;
	private final ModelPart rLeg01b;
	private final ModelPart lLeg02b;
	private final ModelPart rLeg02b;
	private final ModelPart lLeg03b;
	private final ModelPart cube_r3;
	private final ModelPart rLeg03b;
	private final ModelPart cube_r4; */
	public LaCucarachaEntityModel(ModelPart root) {
		this.roach = root.getChild("roach");

		this.abdomin = roach.getChild("abdomin");
		this.lWing01 = roach.getChild("lWing01");
		this.rWing01 = roach.getChild("rWing01");
		this.lWing02 = abdomin.getChild("lWing02");
		this.rWing02 = abdomin.getChild("rWing02");

		this.thorax = roach.getChild("thorax");

		this.head = roach.getChild("head");
		this.sombrero = head.getChild("sombrero");
		this.upperJaw = head.getChild("upperJaw");
		this.lowerJaw = head.getChild("lowerJaw");
		this.lAntenna = head.getChild("lAntenna");
		this.rAntenna = head.getChild("rAntenna");

		this.rLeg01a = roach.getChild("rLeg01a");
		this.rLeg02a = roach.getChild("rLeg02a");
		this.rLeg03a = roach.getChild("rLeg03a");

		this.lLeg01a = roach.getChild("lLeg01a");
		this.lLeg02a = roach.getChild("lLeg02a");
		this.lLeg03a = roach.getChild("lLeg03a");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();

		ModelPartData roach = root.addChild("roach", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData thorax = roach.addChild("thorax", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -2.0F, -8.0F, 17.0F, 10.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -16.0F, -9.0F));

		ModelPartData abdomin = roach.addChild("abdomin", ModelPartBuilder.create().uv(5, 24).cuboid(-9.0F, -1.0F, -2.0F, 19.0F, 11.0F, 29.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -16.0F, -3.0F));

		ModelPartData lWing02 = abdomin.addChild("lWing02", ModelPartBuilder.create().uv(137, 0).cuboid(-6.0F, -2.0F, -1.0F, 19.0F, 0.0F, 46.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, 3.0F, 0.044F, -0.1308F, 0.2561F));

		ModelPartData rWing02 = abdomin.addChild("rWing02", ModelPartBuilder.create().uv(137, 0).mirrored().cuboid(-13.0F, -1.0F, -1.0F, 19.0F, 0.0F, 46.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-3.0F, 0.0F, 3.0F, 0.044F, 0.1308F, -0.2561F));

		ModelPartData eggcase = abdomin.addChild("eggcase", ModelPartBuilder.create().uv(77, 0).cuboid(-5.0F, -6.0F, -3.0F, 11.0F, 11.0F, 21.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 5.0F, 28.0F, -0.3054F, 0.0F, 0.0F));

		ModelPartData head = roach.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, -17.0F));

		ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(90, 83).cuboid(-5.5F, -2.0F, -8.0F, 11.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3054F, 0.0F, 0.0F));

		ModelPartData upperJaw = head.addChild("upperJaw", ModelPartBuilder.create().uv(92, 97).cuboid(-4.5F, -2.0F, -8.0F, 9.0F, 5.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 3.0F, 0.0F));

		ModelPartData lowerJaw = head.addChild("lowerJaw", ModelPartBuilder.create().uv(92, 112).cuboid(-4.0F, -2.0F, -8.0F, 8.0F, 5.0F, 9.0F, new Dilation(-0.01F)), ModelTransform.pivot(0.0F, 5.0F, 0.0F));

		ModelPartData lAntenna = head.addChild("lAntenna", ModelPartBuilder.create().uv(141, 8).cuboid(0.0F, -12.0F, -47.0F, 0.0F, 18.0F, 48.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -1.0F, 0.0F, -0.1409F, -0.3016F, 0.872F));

		ModelPartData rAntenna = head.addChild("rAntenna", ModelPartBuilder.create().uv(141, 8).mirrored().cuboid(0.0F, -12.0F, -47.0F, 0.0F, 18.0F, 48.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-3.0F, -1.0F, 0.0F, -0.1409F, 0.3016F, -0.872F));

		ModelPartData sombrero = head.addChild("sombrero", ModelPartBuilder.create().uv(132, 81).cuboid(-10.0F, -4.5F, -9.0F, 20.0F, 4.0F, 20.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.25F, -2.0F, 0.1309F, 0.0F, 0.0F));

		ModelPartData cube_r2 = sombrero.addChild("cube_r2", ModelPartBuilder.create().uv(154, 111).cuboid(-5.0F, -6.0F, -5.0F, 9.0F, 8.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, 1.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData lLeg01a = roach.addChild("lLeg01a", ModelPartBuilder.create().uv(0, 66).cuboid(-3.0F, -2.0F, -1.0F, 10.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(6.5F, -8.0F, -13.0F, 0.0F, 0.5236F, 0.0F));

		ModelPartData lLeg01b = lLeg01a.addChild("lLeg01b", ModelPartBuilder.create().uv(0, 91).cuboid(-2.0F, -2.0F, -1.5F, 18.0F, 26.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 0.0F, 0.75F, 0.0F, 0.0F, -0.6981F));

		ModelPartData rLeg01a = roach.addChild("rLeg01a", ModelPartBuilder.create().uv(0, 66).mirrored().cuboid(-7.0F, -2.0F, -1.0F, 10.0F, 6.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-6.5F, -8.0F, -13.0F, 0.0F, -0.5236F, 0.0F));

		ModelPartData rLeg01b = rLeg01a.addChild("rLeg01b", ModelPartBuilder.create().uv(0, 91).mirrored().cuboid(-16.0F, -2.0F, -1.5F, 18.0F, 26.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-8.0F, 0.0F, 0.75F, 0.0F, 0.0F, 0.6981F));

		ModelPartData lLeg02a = roach.addChild("lLeg02a", ModelPartBuilder.create().uv(0, 66).cuboid(-3.0F, -2.0F, -1.0F, 10.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(6.5F, -8.0F, -10.0F));

		ModelPartData lLeg02b = lLeg02a.addChild("lLeg02b", ModelPartBuilder.create().uv(0, 91).cuboid(-2.0F, -2.0F, -1.5F, 18.0F, 26.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 0.0F, 0.75F, 0.0F, 0.0F, -0.6981F));

		ModelPartData rLeg02a = roach.addChild("rLeg02a", ModelPartBuilder.create().uv(0, 66).mirrored().cuboid(-7.0F, -2.0F, -1.0F, 10.0F, 6.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-6.5F, -8.0F, -9.9F));

		ModelPartData rLeg02b = rLeg02a.addChild("rLeg02b", ModelPartBuilder.create().uv(0, 91).mirrored().cuboid(-16.0F, -2.0F, -1.5F, 18.0F, 26.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-8.0F, 0.0F, 0.75F, 0.0F, 0.0F, 0.6981F));

		ModelPartData lLeg03a = roach.addChild("lLeg03a", ModelPartBuilder.create().uv(0, 78).cuboid(-3.0F, -2.0F, -1.0F, 19.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(6.5F, -8.0F, -8.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData lLeg03b = lLeg03a.addChild("lLeg03b", ModelPartBuilder.create(), ModelTransform.of(15.0F, 0.0F, 0.75F, 0.6863F, 0.1396F, -0.6046F));

		ModelPartData cube_r3 = lLeg03b.addChild("cube_r3", ModelPartBuilder.create().uv(47, 84).cuboid(-2.0F, -2.0F, -1.5F, 18.0F, 41.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

		ModelPartData rLeg03a = roach.addChild("rLeg03a", ModelPartBuilder.create().uv(0, 78).mirrored().cuboid(-16.0F, -2.0F, -1.0F, 19.0F, 6.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-6.5F, -8.0F, -8.0F, 0.0F, 0.7854F, 0.0F));

		ModelPartData rLeg03b = rLeg03a.addChild("rLeg03b", ModelPartBuilder.create(), ModelTransform.of(-15.0F, 0.0F, 0.75F, 0.6863F, -0.1396F, 0.6046F));

		ModelPartData cube_r4 = rLeg03b.addChild("cube_r4", ModelPartBuilder.create().uv(47, 84).mirrored().cuboid(-16.0F, -2.0F, -1.5F, 18.0F, 41.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

		ModelPartData lWing01 = roach.addChild("lWing01", ModelPartBuilder.create().uv(99, 0).cuboid(-7.0F, -2.0F, -1.0F, 17.0F, 0.0F, 45.0F, new Dilation(0.0F)), ModelTransform.of(4.5F, -16.0F, -4.0F, 0.0438F, -0.0872F, 0.3016F));

		ModelPartData rWing01 = roach.addChild("rWing01", ModelPartBuilder.create().uv(99, 0).mirrored().cuboid(-10.0F, -2.0F, -1.0F, 17.0F, 0.0F, 45.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-4.5F, -16.0F, -4.0F, 0.0438F, 0.0872F, -0.3016F));
		return TexturedModelData.of(modelData, 256, 128);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {

		roach.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public void setAngles(LaCucarachaEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		sombrero.visible = entity.hasSombrero();
		sombrero.getChild("cube_r2").visible = entity.hasSombrero();

		boolean dancing = entity.isDancing();
		int twitchInterval = 30 + entity.getId() % 20;

		if (animationProgress % twitchInterval < 5) {
			float progress = MathHelper.TAU * (animationProgress % 5) / 5F;
			ModelPart antenna = animationProgress % (twitchInterval * 2) >= twitchInterval ? lAntenna : rAntenna;

			antenna.yaw = MathHelper.PI * 0.0125F * MathHelper.sin(progress);
			// lowerJaw.yaw =  MathHelper.PI * 0.0125F * MathHelper.sin(progress);
			lowerJaw.pitch =  MathHelper.PI * 0.025F * MathHelper.sin(progress);

			if(entity.isUsingProjectiles()) {
				lowerJaw.pitch = 0.25f;
				upperJaw.pitch = -0.25f;
			}

		} else {
			rAntenna.yaw = 0;
			lAntenna.yaw = 0;
			lowerJaw.pitch = 0;
			upperJaw.pitch = 0;
		}

		if (dancing) {
			roach.yaw = animationProgress * (entity.getId() % 2 == 0 ? 0.2F : -0.2F);
			roach.pitch = -MathHelper.PI * 0.25F;
			roach.pivotY = 16 + MathHelper.sin(animationProgress * 0.75F) * 2;
			rLeg01a.yaw = -0.6545F;
			lLeg01a.yaw = 0.6545F;
			rLeg02a.yaw = 0;
			lLeg02a.yaw = 0;
			lLeg03a.yaw = -1.2217F;
			rLeg03a.yaw = 1.2217F;
		} else {
			float cycle = (MathHelper.cos(limbAngle * 0.6662F * 2.0F)) * 0.4F * limbDistance;
			roach.yaw = 0;
			roach.pitch = 0;
			roach.pivotY = 22;
			rLeg01a.yaw = -0.6545F - cycle;
			lLeg01a.yaw = 0.6545F - cycle;
			rLeg02a.yaw = cycle;
			lLeg02a.yaw = cycle;
			lLeg03a.yaw = -0.98F - cycle * 1.1f;
			rLeg03a.yaw = 0.98F - cycle * 1.1f;

			if (entity.isFlying()) {
				rWing01.pitch = 0.8F;
				rWing01.yaw = -0.06F - MathHelper.PI * 0.35F;

				rWing02.pitch = 0.0436F + (1 + MathHelper.cos(animationProgress * 2)) * 0.35F;
				rWing02.yaw = -0.0436F - MathHelper.PI * 0.35F;

				lWing01.pitch = 0.8F;
				lWing01.yaw = 0.06F + MathHelper.PI * 0.35F;

				lWing02.pitch = 0.0436F + (1 + MathHelper.cos(animationProgress * 2)) * 0.35F;
				lWing02.yaw = 0.0436F + MathHelper.PI * 0.35F;
			} else {
				rWing01.pitch = 0.045F;
				rWing01.yaw = -0.045F;

				lWing01.pitch = 0.045F;
				lWing01.yaw = 0.045F;

				rWing02.pitch = 0.045F;
				rWing02.yaw = -0.045F;

				lWing02.pitch = 0.045F;
				lWing02.yaw = 0.045F;
			}
		}
	}
}
