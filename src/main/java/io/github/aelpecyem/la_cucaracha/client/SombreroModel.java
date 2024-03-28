package io.github.aelpecyem.la_cucaracha.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

// Made with Blockbench 4.9.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
// #todo ADD
public class SombreroModel extends BipedEntityModel<LivingEntity> {
	private final ModelPart armorHead;
	private final ModelPart sombrero;

	public SombreroModel(ModelPart root) {
		super(root);
		this.armorHead = root.getChild(EntityModelPartNames.HEAD).getChild("armorHead");
		this.sombrero = armorHead.getChild("sombrero");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bipedHead = modelPartData.getChild(EntityModelPartNames.HEAD);
		ModelPartData armorHead = bipedHead.addChild("armorHead", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData sombrero = armorHead.addChild("sombrero", ModelPartBuilder.create().uv(70, 25).cuboid(-7.0F, -2.85F, -7.0F, 14.0F, 2.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -7.25F, 0.0F));
		ModelPartData cube_r1 = sombrero.addChild("cube_r1", ModelPartBuilder.create().uv(80, 0).cuboid(-3.0F, -4.5F, -3.0F, 5.0F, 3.0F, 5.0F, new Dilation(0.0F))
			.uv(77, 10).cuboid(-4.0F, -1.5F, -4.0F, 7.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, 1.0F, 0.0F, -0.7854F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
