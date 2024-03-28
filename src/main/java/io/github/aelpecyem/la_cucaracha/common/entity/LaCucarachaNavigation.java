package io.github.aelpecyem.la_cucaracha.common.entity;

import mod.azure.azurelib.ai.pathing.AzureNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class LaCucarachaNavigation extends AzureNavigation {
	public LaCucarachaNavigation(MobEntity entity, World world) {
		super(entity, world);
	}
}
