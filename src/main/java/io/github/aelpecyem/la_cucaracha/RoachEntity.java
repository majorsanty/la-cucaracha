package io.github.aelpecyem.la_cucaracha;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer.Builder;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.DynamicGameEventListener;
import net.minecraft.world.event.listener.GameEventListener;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

public class RoachEntity extends PathAwareEntity {

	private static final TrackedData<Integer> SIZE = DataTracker.registerData(RoachEntity.class, TrackedDataHandlerRegistry.INTEGER);
	/**
	 * Roach Flags Indexes:
	 * 0 - Dancing
	 * 1 - Climbing
	 * 2 - Flying
	 * 3 - ???
	 * 4,5 - Variant
	 */
	private static final TrackedData<Byte> ROACH_FLAGS = DataTracker.registerData(RoachEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> TARGET_FOOD_ENTITY_ID = DataTracker.registerData(RoachEntity.class, TrackedDataHandlerRegistry.INTEGER);

	private final DynamicGameEventListener<JukeboxListener> jukeboxListener;
	private boolean swarmMode, summoned = false;
	private UUID specificTarget;
	private BlockPos trackedJukebox;
	private int eatingTicks;

	protected RoachEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
		PositionSource positionSource = new EntityPositionSource(this, this.getStandingEyeHeight());
		this.jukeboxListener = new DynamicGameEventListener<>(new JukeboxListener(positionSource, GameEvent.JUKEBOX_PLAY.getRange()));
		((MobNavigation) getNavigation()).setAvoidSunlight(true);
		getNavigation().setRangeMultiplier(0.1F);
	}

	public static Builder createRoachAttributes() {
		return SilverfishEntity.createSilverfishAttributes();
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25D) {
			@Override
			public boolean canStart() {
				return !isAggressive() && super.canStart();
			}
		});
		this.goalSelector.add(1, new EatFoodGoal());
		this.goalSelector.add(2, new WanderAroundGoal(this, 1.8, 30, true) {
			@Nullable
			@Override
			protected Vec3d getWanderTarget() {
				return NoPenaltyTargeting.find(this.mob, 3, 1);
			}
		});
		this.goalSelector.add(3, new GroupTogetherGoal());
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.5, false) {
			@Override
			public boolean canStart() {
				return isAggressive() && super.canStart();
			}
		});
		this.targetSelector.add(1, new RevengeGoal(this) {
			@Override
			public boolean canStart() {
				return isAggressive() && super.canStart();
			}
		}.setGroupRevenge());
		this.targetSelector.add(2, new TargetGoal<>(this, LivingEntity.class, true, target ->
			!(target instanceof RoachEntity) &&
				((specificTarget != null && target.getUuid().equals(specificTarget)) || target.isHolding(ItemStack::isFood))) {
			@Override
			public boolean canStart() {
				return isAggressive() && super.canStart();
			}
		});

	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		dataTracker.startTracking(SIZE, 1);
		dataTracker.startTracking(ROACH_FLAGS, (byte) 0b0000_0000);
		dataTracker.startTracking(TARGET_FOOD_ENTITY_ID, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (summoned && age > 300) {
			kill();
		}
		if (age % 20 == 0 && isDancing() && trackedJukebox != null) {
			if (!(world.getBlockState(trackedJukebox).isOf(Blocks.JUKEBOX)
				&& world.getBlockState(trackedJukebox).get(JukeboxBlock.HAS_RECORD)
				&& trackedJukebox.isWithinDistance(getBlockPos(), GameEvent.JUKEBOX_PLAY.getRange()))) {
				setDancing(false, null);
			}
		}
		int targetFood = getTargetFoodEntityId();

		if (targetFood > -1) {
			Entity food = getTargetFoodEntity();
			if (getMainHandStack().isEmpty()) {
				if (food instanceof ItemEntity item && !item.isRemoved() && item.getStack().isFood()) {
					setStackInHand(Hand.MAIN_HAND, item.getStack().split(1));
				} else {
					setTargetFoodEntityId(-1);
				}
			} else {
				ItemStack stack = getMainHandStack();
				if (stack.isFood()) {
					if (world.isClient) {
						world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), getX(), getY(), getZ(),
										  random.nextGaussian() * 0.1, random.nextFloat() * 0.1, random.nextGaussian() * 0.1);
					} else {
						eatingTicks++;
						if (eatingTicks > 0) {
							this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.1F + 0.05F * (float) this.random.nextInt(2),
										   (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.2F);
							if (eatingTicks > stack.getMaxUseTime()) {
								if (stack.getItem().getRecipeRemainder() != null) {
									dropItem(stack.getItem().getRecipeRemainder());
								}
								heal(stack.getItem().getFoodComponent().getHunger());
								eatFood(world, stack);
								eatingTicks = -10;
							}
						}
					}
				} else {
					dropStack(stack);
					setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
				}
			}
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		super.onPlayerCollision(player);
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 0));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Size", this.getSize() - 1);
		nbt.putBoolean("Summoned", summoned);
		nbt.putByte("RoachFlags", dataTracker.get(ROACH_FLAGS));
		if (trackedJukebox != null) {
			nbt.put("JukeboxPos", NbtHelper.fromBlockPos(trackedJukebox));
		}
		if (specificTarget != null) {
			nbt.putUuid("SpecificTarget", specificTarget);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.setSize(nbt.getInt("Size") + 1);
		super.readCustomDataFromNbt(nbt);
		this.summoned = nbt.getBoolean("Summoned");
		dataTracker.set(ROACH_FLAGS, nbt.getByte("RoachFlags"));
		this.trackedJukebox = nbt.contains("JukeboxPos") ? NbtHelper.toBlockPos(nbt.getCompound("JukeboxPos")) : null;
		if (nbt.contains("SpecificTarget")) {
			this.specificTarget = nbt.getUuid("SpecificTarget");
		}
	}

	public void setSpecificTarget(UUID specificTarget) {
		this.specificTarget = specificTarget;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!world.isClient) {
			setRoachClimbing(horizontalCollision);
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
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
								 @Nullable EntityData entityData,
								 @Nullable NbtCompound entityNbt) {
		setSize(1 + random.nextInt(4));
		setVariant(random.nextInt(4));
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

	public int getVariant() {
		return (getRoachFlag(4) ? 1 : 0) + (getRoachFlag(5) ? 2 : 0);
	}

	public void setVariant(int variant) {
		switch (variant) {
			case 0 -> {
				setRoachFlag(4, false);
				setRoachFlag(5, false);
			}
			case 1 -> {
				setRoachFlag(4, true);
				setRoachFlag(5, false);
			}
			case 2 -> {
				setRoachFlag(4, false);
				setRoachFlag(5, true);
			}
			case 3 -> {
				setRoachFlag(4, true);
				setRoachFlag(5, true);
			}
		}
	}

	public void setDancing(boolean dancing, BlockPos trackedJukebox) {
		setRoachFlag(0, dancing);
		this.trackedJukebox = trackedJukebox;
	}

	protected void setRoachFlag(int index, boolean value) {
		byte b = this.dataTracker.get(ROACH_FLAGS);
		if (value) {
			this.dataTracker.set(ROACH_FLAGS, (byte) (b | 1 << index));
		} else {
			this.dataTracker.set(ROACH_FLAGS, (byte) (b & ~(1 << index)));
		}
	}

	public boolean isDancing() {
		return getRoachFlag(0);
	}

	protected boolean getRoachFlag(int index) {
		return (this.dataTracker.get(ROACH_FLAGS) & 1 << index) != 0;
	}

	public int getTargetFoodEntityId() {
		return this.dataTracker.get(TARGET_FOOD_ENTITY_ID);
	}

	public void setTargetFoodEntityId(int id) {
		this.dataTracker.set(TARGET_FOOD_ENTITY_ID, id);
	}

	public Entity getTargetFoodEntity() {
		return world.getEntityById(getTargetFoodEntityId());
	}

	public boolean isAggressive() {
		return isSwarmMode() || isSummoned();
	}

	public boolean isSummoned() {
		return summoned;
	}

	public void setSummoned(boolean summoned) {
		this.summoned = summoned;
	}

	public boolean isSwarmMode() {
		return swarmMode;
	}

	public void setSwarmMode(boolean swarmMode) {
		this.swarmMode = swarmMode;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (super.damage(source, amount)) {
			setDancing(false, trackedJukebox);
			setTargetFoodEntityId(-1);
			return true;
		}
		return false;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return LaCucaracha.ROACH_HURT_SOUND_EVENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return LaCucaracha.ROACH_DEATH_SOUND_EVENT;
	}

	@Override
	public boolean isClimbing() {
		return isRoachClimbing();
	}

	public boolean isRoachClimbing() {
		return getRoachFlag(1);
	}

	public void setRoachClimbing(boolean climbing) {
		setRoachFlag(1, climbing);
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	protected boolean isImmobile() {
		return isDancing() || super.isImmobile();
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

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(LaCucaracha.ROACH_SCURRY_SOUND_EVENT, 0.1F, 1.0F);
	}

	@Override
	public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerWorld> updater) {
		super.updateDynamicGameEventListener(updater);
		if (world instanceof ServerWorld serverWorld) {
			updater.accept(this.jukeboxListener, serverWorld);
		}
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

	private class EatFoodGoal extends Goal {
		Path path;
		int checkTicks = 0;
		Entity target;

		public EatFoodGoal() {
			setControls(EnumSet.of(Control.TARGET, Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (checkTicks-- <= 0 && getTarget() == null) {
				Entity foodTarget = getTargetFoodEntity();
				if (foodTarget != null && distanceTo(foodTarget) < 2) {
					return false;
				}
				List<Entity> foods = world.getOtherEntities(RoachEntity.this, getBoundingBox().expand(16),
									   entity -> entity instanceof ItemEntity i && i.getStack().isFood() && i.isOnGround());
				foods.sort(Comparator.comparingDouble(e -> e.distanceTo(RoachEntity.this)));
				for (Entity food : foods) {
					this.path = getNavigation().findPathTo(food, 16);
					if (path != null) {
						target = food;
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean shouldContinue() {
			return path != null && !path.isFinished() && target != null && !target.isRemoved();
		}

		@Override
		public void start() {
			getNavigation().startMovingTo(target.getX(), target.getY() + 1, target.getZ(), 1.5F);
		}

		@Override
		public void stop() {
			checkTicks = 20;
			target = null;
		}

		@Override
		public void tick() {
			super.tick();
			if (checkTicks-- <= 0) {
				this.path = getNavigation().findPathTo(target, 16);
				if (path == null) {
					stop();
					return;
				}
				checkTicks = 20;
				getNavigation().startMovingTo(target.getX(), target.getY() + 1, target.getZ(), 1.5F);
			}
			if (distanceTo(target) < 1.5) {
				setTargetFoodEntityId(target.getId());
				stop();
			}
		}
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
		public void start() {
			getNavigation().startMovingAlong(path, 1.5F);
			checkTicks = getTickCount(80);
		}

		@Override
		public void stop() {
			this.path = null;
			ticksRunning = 0;
		}

		@Override
		public void tick() {
			super.tick();
			ticksRunning++;
		}
	}

	private class JukeboxListener implements GameEventListener {

		private final PositionSource positionSource;
		private final int range;

		public JukeboxListener(PositionSource positionSource, int range) {
			this.positionSource = positionSource;
			this.range = range;
		}

		@Override
		public PositionSource getPositionSource() {
			return this.positionSource;
		}

		@Override
		public int getRange() {
			return this.range;
		}

		@Override
		public boolean listen(ServerWorld world, GameEvent.Message eventMessage) {
			if (eventMessage.getEvent() == GameEvent.JUKEBOX_PLAY && (lastAttackedTicks > 100 || lastAttackedTicks <= 0)) {
				RoachEntity.this.setDancing(true, new BlockPos(eventMessage.getSourcePos()));
				return true;
			} else if (eventMessage.getEvent() == GameEvent.JUKEBOX_STOP_PLAY) {
				RoachEntity.this.setDancing(false, new BlockPos(eventMessage.getSourcePos()));
				return true;
			} else {
				return false;
			}
		}
	}
}
