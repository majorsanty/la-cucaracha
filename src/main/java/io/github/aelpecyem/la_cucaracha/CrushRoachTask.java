package io.github.aelpecyem.la_cucaracha;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.VisibleLivingEntitiesCache;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

import com.google.common.collect.ImmutableMap;

public class CrushRoachTask extends Task<VillagerEntity> {
	private RoachEntity targetRoach;
	public CrushRoachTask() {
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
		return targetRoach != null && (targetRoach.distanceTo(entity) > 1 || entity.getVelocity().getY() > 0);
	}

	@Override
	protected void run(ServerWorld world, VillagerEntity entity, long time) {
		super.run(world, entity, time);
		entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(targetRoach, 1, 0));
	}

	@Override
	protected void keepRunning(ServerWorld world, VillagerEntity entity, long time) {
		super.keepRunning(world, entity, time);
		if (targetRoach != null && entity.distanceTo(targetRoach) < 2 && entity.age % 20 == 0) {
			System.out.println(entity.distanceTo(targetRoach));
			entity.getJumpControl().setActive();
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, VillagerEntity entity, long time) {
		super.finishRunning(world, entity, time);
		if (targetRoach != null && entity.distanceTo(targetRoach) < 1) {
			targetRoach.damage(DamageSource.CRAMMING, targetRoach.getMaxHealth());
		}
	}

	private LivingEntity findRoach(VillagerEntity villager) {
		VisibleLivingEntitiesCache visibleLivingEntitiesCache = villager.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
		return visibleLivingEntitiesCache.method_38975(entity -> entity instanceof RoachEntity).orElse(null);
	}
}
