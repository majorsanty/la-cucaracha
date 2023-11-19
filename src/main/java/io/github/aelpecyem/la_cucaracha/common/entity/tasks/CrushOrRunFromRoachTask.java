package io.github.aelpecyem.la_cucaracha.common.entity.tasks;

import com.google.common.collect.ImmutableMap;
import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import io.github.aelpecyem.la_cucaracha.common.entity.LaCucarachaEntity;
import io.github.aelpecyem.la_cucaracha.common.entity.RoachEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class CrushOrRunFromRoachTask extends MultiTickTask<VillagerEntity> {
	private RoachEntity targetRoach;

	public CrushOrRunFromRoachTask() {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
			MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
	}

	@Override
	protected boolean shouldRun(ServerWorld world, VillagerEntity entity) {
		targetRoach = (RoachEntity) findRoach(entity);
		return targetRoach != null;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, VillagerEntity entity, long time) {
		int chance = Math.abs(entity.getUuid().hashCode() % 10);

		return chance >= 8 ?
			targetRoach != null && (targetRoach.distanceTo(entity) > 1 && targetRoach.distanceTo(entity) < 40)
			: (targetRoach != null && (targetRoach.distanceTo(entity) > 1 || entity.getVelocity().getY() > 0));
	}

	@Override
	protected void run(ServerWorld world, VillagerEntity entity, long time) {
		super.run(world, entity, time);
		int chance = Math.abs(entity.getUuid().hashCode() % 10);


		if (chance >= 8) {
			Vec3d vec3d4 = FuzzyTargeting.findFrom(entity, 32, 12, entity.getPos());
			if (vec3d4 != null)
				entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d4, 1f, 0));
		} else {
			entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(targetRoach, 1.5F, 0));
		}
	}

	@Override
	protected void keepRunning(ServerWorld world, VillagerEntity entity, long time) {
		super.keepRunning(world, entity, time);
		if (targetRoach != null && entity.distanceTo(targetRoach) < 2 && entity.age % 20 == 0) {
			entity.getJumpControl().setActive();
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, VillagerEntity entity, long time) {
		super.finishRunning(world, entity, time);
		if (targetRoach != null && !(targetRoach instanceof LaCucarachaEntity) && entity.distanceTo(targetRoach) < 1.5f) {
			targetRoach.damage(LaCucaracha.create(DamageTypes.CRAMMING, world), targetRoach.getMaxHealth());
		}
	}

	private LivingEntity findRoach(VillagerEntity villager) {
		LivingTargetCache visible = villager.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
		return visible.findFirst(RoachEntity.class::isInstance).orElse(null);
	}
}
