package io.github.aelpecyem.la_cucaracha.common.entity.tasks;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;

public class FindTargetGoal extends ActiveTargetGoal {
	public FindTargetGoal(MobEntity mob, Class targetClass, boolean checkVisibility) {
		super(mob, targetClass, checkVisibility);
	}

	@Override
	protected void findClosestTarget() {
		super.findClosestTarget();
	}
}
