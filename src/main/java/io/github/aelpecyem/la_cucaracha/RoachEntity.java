package io.github.aelpecyem.la_cucaracha;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer.Builder;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public class RoachEntity extends PathAwareEntity {

	private static final TrackedData<Integer> SIZE = DataTracker.registerData(RoachEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private boolean swarmMode, summoned = false;
	protected RoachEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
	}

	public static Builder createRoachAttributes() {
		return SilverfishEntity.createSilverfishAttributes();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		dataTracker.startTracking(SIZE, 1);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25D) {
			@Override
			public boolean canStart() {
				return !isSwarmMode() && super.canStart();
			}
		});
		this.goalSelector.add(1, new WanderAroundGoal(this, 1.8, 30, true) {
			@Nullable
			@Override
			protected Vec3d getWanderTarget() {
				return NoPenaltyTargeting.find(this.mob, 3, 1);
			}
		});
		this.goalSelector.add(2, new GroupTogetherGoal());
		this.goalSelector.add(3, new MeleeAttackGoal(this, 1.5, false) {
			@Override
			public boolean canStart() {
				return isSwarmMode() && super.canStart();
			}
		});
		this.targetSelector.add(1, new RevengeGoal(this) {
			@Override
			public boolean canStart() {
				return isSwarmMode() && super.canStart();
			}
		}.setGroupRevenge());
		this.targetSelector.add(2, new TargetGoal<>(this, PlayerEntity.class, true) {
			@Override
			public boolean canStart() {
				return isSwarmMode() && super.canStart();
			}
		});

	}

	@Override
	public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!summoned && itemStack.isOf(Items.GLASS_BOTTLE)) {
			player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 1.0F, 1.0F);
			ItemStack roachBottle = LaCucaracha.BOTTLED_ROACH_ITEM.getDefaultStack();
			BottledRoachItem.writeRoachToTag(roachBottle, this);
			remove(RemovalReason.DISCARDED);
			player.setStackInHand(hand, ItemUsage.exchangeStack(itemStack, player, roachBottle, false));
			return ActionResult.success(this.world.isClient);
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		if (age % 40 == 0) {
			List<Entity> roaches = world.getOtherEntities(this, getBoundingBox().expand(5), entity -> entity instanceof RoachEntity);
			if (age % 40 < 20) {
				this.setSwarmMode(roaches.size() >= 3);
			} else {
				if (!isSwarmMode()) {
					for (Entity entity : roaches) {
						if (entity instanceof RoachEntity roach && roach.isSwarmMode()) {
							this.setSwarmMode(true);
							break;
						}
					}
				}
			}
		}

		if (summoned && age > 300) {
			kill();
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Size", this.getSize() - 1);
		nbt.putBoolean("Summoned", summoned);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.setSize(nbt.getInt("Size") + 1);
		super.readCustomDataFromNbt(nbt);
		this.summoned = nbt.getBoolean("Summoned");
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
								 @Nullable EntityData entityData,
								 @Nullable NbtCompound entityNbt) {
		setSize(1 + random.nextInt(4));
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	public int getSize() {
		return this.dataTracker.get(SIZE);
	}

	public void setSize(int size) {
		int i = MathHelper.clamp(size, 1, 127);
		this.dataTracker.set(SIZE, i);
		this.refreshPosition();
		this.calculateDimensions();
		this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(5 + i);

	}

	@Override
	public void calculateDimensions() {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		super.calculateDimensions();
		this.setPosition(d, e, f);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (SIZE.equals(data)) {
			this.calculateDimensions();
			this.setYaw(this.headYaw);
			this.bodyYaw = this.headYaw;
			if (this.isTouchingWater() && this.random.nextInt(20) == 0) {
				this.onSwimmingStart();
			}
		}

		super.onTrackedDataSet(data);
	}

	public EntityDimensions getDimensions(EntityPose pose) {
		return super.getDimensions(pose).scaled(1);
	}

	public void setSummoned(boolean summoned) {
		this.summoned = summoned;
	}

	public boolean isSummoned() {
		return summoned;
	}

	public void setSwarmMode(boolean swarmMode) {
		this.swarmMode = swarmMode;
	}

	public boolean isSwarmMode() {
		return swarmMode;
	}

	public boolean isAggressive() {
		return isSwarmMode() || isSummoned();
	}

	private class GroupTogetherGoal extends Goal {
		private Path path;
		private int checkTicks = 0;
		private int ticksRunning = 0;
		@Override
		public boolean canStart() {
			if (checkTicks-- <= 0) {
				List<Entity> roaches = world.getOtherEntities(RoachEntity.this, getBoundingBox().expand(8), entity -> entity instanceof RoachEntity);
				for (Entity roach : roaches) {
					this.path = getNavigation().findPathTo(roach, 8);
					if (path != null) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean shouldContinue() {
			return path != null && !path.isFinished() && ticksRunning < 20;
		}

		@Override
		public void stop() {
			this.path = null;
			ticksRunning = 0;
		}

		@Override
		public void start() {
			getNavigation().startMovingAlong(path, 1.5F);
			checkTicks = getTickCount(80);
		}

		@Override
		public void tick() {
			super.tick();
			ticksRunning++;
		}
	}
}
