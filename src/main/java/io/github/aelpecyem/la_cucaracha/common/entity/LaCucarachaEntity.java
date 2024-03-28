package io.github.aelpecyem.la_cucaracha.common.entity;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import mod.azure.azurelib.ai.pathing.AzureNavigation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.Vibrations;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class LaCucarachaEntity extends RoachEntity implements RangedAttackMob, Vibrations {

	// passive or agressive
	static final TrackedData<Boolean> PERSONALITY = DataTracker.registerData(LaCucarachaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	static final TrackedData<Boolean> SOMBRERO = DataTracker.registerData(LaCucarachaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	static final TrackedData<Integer> PROJECTILE_USAGE = DataTracker.registerData(LaCucarachaEntity.class, TrackedDataHandlerRegistry.INTEGER);

	//#TODO vibrations

	private final Vibrations.ListenerData vibrationListenerData = new Vibrations.ListenerData();
	//	private final Vibrations.Callback vibrationCallback = new VibrationCallback();
	private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.5, false);
	boolean canGrow = true;
	private CustomProjectileAttackGoal spitAttackGoal = new CustomProjectileAttackGoal(this, 1.25, 5, 10, 24F) {

		@Override
		public boolean canStart() {
			return super.canStart() && !(getTarget() instanceof EndermanEntity) && !(getTarget() instanceof SpiderEntity);
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue();
		}
	};


	public LaCucarachaEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0F);
		this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0F);
		this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
		this.setStepHeight(2.5F);

	}

	public static DefaultAttributeContainer.Builder createCucarachaAttributes() {
		return HostileEntity.createHostileAttributes().
			add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.41)
			.add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6000000238418579)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 9D)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 256D)
			.add(EntityAttributes.GENERIC_ARMOR, 16.0)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.85)
			.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.75)
			.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 5.0);
	}

	public CustomProjectileAttackGoal getRangedGoal() {
		return spitAttackGoal;
	}

	@Override
	public ListenerData getVibrationListenerData() {
		return vibrationListenerData;
	}

	@Override
	public Callback getVibrationCallback() {
		return null;
	}

	@Override
	protected void initDataTracker() {
		dataTracker.startTracking(SOMBRERO, false);
		dataTracker.startTracking(PROJECTILE_USAGE, 0);
		super.initDataTracker();

	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putBoolean("Sombrero", hasSombrero());
		super.writeCustomDataToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		dataTracker.set(SOMBRERO, nbt.getBoolean("Sombrero"));
		super.readCustomDataFromNbt(nbt);
	}

	@Override
	public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
		return ActionResult.SUCCESS;
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
								 @Nullable EntityData entityData,
								 @Nullable NbtCompound entityNbt) {
		setSize(0); // size up to 10
		setVariant(0);
		hasWings = true;
		return entityData;
	}

	@Override
	public boolean onKilledOther(ServerWorld world, LivingEntity other) {
		this.heal(other instanceof RoachEntity ? other.getMaxHealth() * 4f : other.getMaxHealth() + this.getMaxHealth() / 20f);
		this.playSound(LaCucaracha.LA_CUCARACHA_ATTACK_SOUND_EVENT, 1F, 1.0F);

		if (world.random.nextFloat() > 0.9 && this.getSize() < maxSize()) {
			this.setSize(getSize() + 1);
			if (world.random.nextFloat() > 0.5 && !hasSombrero()) {
				this.dataTracker.set(SOMBRERO, true);
			}
			if (this.age % 20 == 0)
				this.playSound(LaCucaracha.LA_CUCARACHA_EVOLVE_SOUND_EVENT, 4F, 1.0F);
		}


		return super.onKilledOther(world, other);
	}

	@Override
	public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
		return effect.getEffectType().isBeneficial() || effect.getEffectType() == StatusEffects.WITHER;
	}

	protected EntityNavigation createNavigation(World world) {
		return new AzureNavigation(this, world);
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		super.onPlayerCollision(player);
		if (!getWorld().isClient && !player.isInvulnerable() && !player.isCreative()) {
			player.addStatusEffect(new StatusEffectInstance(LaCucaracha.CAUSTIC_FLUIDS, 60, 0));
		}
	}

	public boolean isUsingProjectiles() {
		return this.dataTracker.get(PROJECTILE_USAGE) >= 0 && this.isAggressive();
	}

	int ticksSearching;
	@Override
	public void tick() {
		// this.setVelocity(this.getVelocity().getX(), 0.005, this.getVelocity().getZ());

		if (eatingTicks > 0) {
			if (canGrow) {
				canGrow = false;
				if (getWorld().random.nextFloat() > 0.95) {
					this.setSize(getSize() + 1);
					this.playSound(LaCucaracha.LA_CUCARACHA_EVOLVE_SOUND_EVENT, 0.5F, 1.0F);
				}
			}
		} else canGrow = true;

		if (this.isUsingProjectiles())
			this.dataTracker.set(PROJECTILE_USAGE, this.dataTracker.get(PROJECTILE_USAGE) + 1);


		if(this.getSize() > 2 && this.random.nextFloat() > 0.95 && this.getMaxHealth() * 0.8 >= this.getHealth() && !this.getWorld().isClient) {
			RoachEntity.spawnRoaches(this.getWorld(), this.getTarget() != null ? this.getTarget() : null, this.getPos(), random, this.random.nextBetween(8,15), true);
		}


		// this.dataTracker.set(SOMBRERO, true);
		// this.setSize(10);

		if (this.getTarget() != null) {

			// #TODO if navigation ticks on roughly the same position are >= 20, break blocks around him?
			// #TODO probably limit block breaking ONLY when there's no path towards an entity. Prolly for a future update?

			boolean spitter = this.getUuid().hashCode() % 2 == 0;
			if (this.isAggressive()) {
                System.out.println(this.spitAttackGoal.getSeenTargetTicks());

				if(this.spitAttackGoal.getSeenTargetTicks() >= 80) {
					this.dataTracker.set(PROJECTILE_USAGE, spitter ?  -30 : -60);
				}

                if (spitter) {
                    if (((this.getTarget().distanceTo(this) > 3 && this.getTarget().distanceTo(this) < 24) || !isOnGround()) && this.isUsingProjectiles()) {
						// this.spitAttackGoal.setSeenTargetTicks(0);
						this.goalSelector.remove(this.meleeAttackGoal);
						this.goalSelector.add(2, this.getRangedGoal());
					} else {
						// this.spitAttackGoal.setSeenTargetTicks(0);
						this.goalSelector.remove(this.getRangedGoal());
						this.goalSelector.add(2, this.meleeAttackGoal);
					}

				} else {
                    if (!isOnGround() && this.isUsingProjectiles()) {
						this.goalSelector.remove(this.meleeAttackGoal);
						this.goalSelector.add(2, this.getRangedGoal());
					} else {
						// this.spitAttackGoal.setSeenTargetTicks(0);
						this.goalSelector.remove(this.getRangedGoal());
						this.goalSelector.add(2, this.meleeAttackGoal);
					}
				}
			}
			ticksSearching++;

			breakBlocks();
		}

		super.tick();
	}

	protected float getBaseMovementSpeedMultiplier() { // run from water pls
		return isAggressive() ? 0.9F : super.getBaseMovementSpeedMultiplier();
	}


	public void breakBlocks() {

		if (getSize() >= 1 && ticksSearching >= 20 && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
			int i = MathHelper.floor(this.getY());
			int j = MathHelper.floor(this.getX());
			int k = MathHelper.floor(this.getZ());
			boolean bl = false;

			int size = 1 + this.getSize() / 5;
			for (int l = -size; l <= size; ++l) {
				for (int m = -size; m <= size; ++m) {
					int t = (getTarget().getPos().y + 1 >= this.getPos().y ? 3 : 1) + (this.isClimbing() ? 1 : 0);
					int t2 = (getTarget().getPos().y - 1 < this.getPos().y ? -3 : -1);

					for (int n = t2; n <= t; ++n) {
						int x = j + l;
						int y = i + n;
						int z = k + m;
						BlockPos blockPos = new BlockPos(x, y, z);
						BlockState blockState = this.getWorld().getBlockState(blockPos);
						if (canDestroy(blockState, getSize() >= 1 && getSize() <= 3 ? 0 : getSize() <= 5 ? 1 : 2)) {
							bl = this.getWorld().breakBlock(blockPos, true, this) || bl;
						}
					}
				}
			}
		}
		if (getSize() >= 5) {
			int a = MathHelper.floor(this.getY());
			int b = MathHelper.floor(this.getX());
			int c = MathHelper.floor(this.getZ());
			boolean bl = false;

			int size = 5;
			for (int i = -size; i <= size; ++i) {
				for (int j = -size; j <= size; ++j) {
					for (int k = -size; k <= size; ++k) {
						int x = b + i;
						int y = a + k;
						int z = c + j;
						BlockPos blockPos = new BlockPos(x, y, z);
						BlockState blockState = this.getWorld().getBlockState(blockPos);
						if (canDestroy(blockState, 0)) {
							bl = this.getWorld().breakBlock(blockPos, true, this) || bl;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isPushedByFluids() {
		return false;
	}

	@Override
	protected float getVelocityMultiplier() {
		boolean spitter = this.getUuid().hashCode() % 2 == 0;
		return spitter ? super.getVelocityMultiplier() : 1.1f;
	}

	public boolean canDestroy(BlockState block, int i) {
		if (i == 0)
			return !block.isAir() && !block.isIn(BlockTags.WITHER_IMMUNE) && block.getBlock().getHardness() <= Blocks.ACACIA_LEAVES.getHardness();

		if (getSize() >= 4 && getSize() <= 5)
			return !block.isAir() && !block.isIn(BlockTags.WITHER_IMMUNE) && !block.isIn(BlockTags.NEEDS_STONE_TOOL) && !block.isIn(BlockTags.PICKAXE_MINEABLE) && (block.getBlock().getHardness() <= Blocks.OAK_WOOD.getHardness());

		return !block.isAir() && !block.isIn(BlockTags.WITHER_IMMUNE) && block.getBlock().getHardness() <= Blocks.DEEPSLATE.getHardness();
	}

	public boolean hasSombrero() {
		return this.dataTracker.get(SOMBRERO);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(LaCucaracha.LA_CUCARACHA_SCURRY_SOUND_EVENT, 1F, 1.0F);
	}

	@Override
	protected void playHurtSound(DamageSource source) {
		this.playSound(LaCucaracha.LA_CUCARACHA_HURT_SOUND_EVENT, 1 + 3F * random.nextFloat(), 1.0F + random.nextFloat());
	}

	@Override
	public void travel(Vec3d movementInput) {
		super.travel(movementInput);

		if (this.horizontalCollision && this.isClimbing()) {
			setVelocity(getVelocity().getX(), 0.35, getVelocity().getZ());
		}
	}

	@Override
	public void tickMovement() {
		super.tickMovement();

		double fallSpeed = -0.1 + -Math.min(this.fallDistance, 8) / 80 + (this.getTarget() != null && this.getPos().getY() > this.getTarget().getPos().getY() ? -5f : 0);
		if (isFlying() && !isClimbing() && getVelocity().getY() < fallSpeed) {
			setVelocity(getVelocity().getX() * 1.05, fallSpeed, getVelocity().getZ() * 1.05);
		}

		if (!getWorld().isClient) {
			setRoachClimbing(horizontalCollision);
			if (hasWings) {
				setFlying(!isOnGround());
			}
		}

		if (!isOnGround() && this.age % 20 == 0)
			this.playSound(LaCucaracha.LA_CUCARACHA_FLY_SOUND_EVENT, 1 + 3F * random.nextFloat(), 1.0F + random.nextFloat());

	}

	public boolean isAggressive() {
		boolean hunting = this.getSize() < (hasSombrero() ? 10 : 5);
		boolean recoverHealth = this.getMaxHealth() > this.getHealth();
		boolean runAway = this.getMaxHealth() / 3 <= this.getHealth();


		return (hunting || recoverHealth) && runAway;
	}

	@Override
	public void applyDamageEffects(LivingEntity attacker, Entity target) {
		super.applyDamageEffects(attacker, target);
		float dmg = 6 + 1F * getSize();
		if (target instanceof LivingEntity livingEntity) {
			livingEntity.hurtTime = livingEntity.maxHurtTime = 0;
		}

		this.playSound(LaCucaracha.LA_CUCARACHA_ATTACK_SOUND_EVENT, 1.0F, 1.0F);
		target.damage(LaCucaracha.create(LaCucaracha.ROACH, getWorld()), dmg);
	}

	@Override
	public void setSize(int size) {
		int i = MathHelper.clamp(size, 0, maxSize());
		this.dataTracker.set(SIZE, i);
		this.refreshPosition();
		this.calculateDimensions();
		if(size == 5) {
			this.goalSelector.remove(this.meleeAttackGoal);
			this.goalSelector.remove(this.spitAttackGoal);
			this.spitAttackGoal = new CustomProjectileAttackGoal(this, 1.25, 5, 10, 24F) {

				@Override
				public boolean canStart() {
					return super.canStart() && !(getTarget() instanceof EndermanEntity) && !(getTarget() instanceof SpiderEntity);
				}

				@Override
				public boolean shouldContinue() {
					return super.shouldContinue();
				}
			};
		}

		Multimap<EntityAttribute, EntityAttributeModifier> map = ArrayListMultimap.create();
		map.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(UUID.fromString("d54c0487-edb5-4496-a0ab-609801d77a91"), "health modif", i / 10f, EntityAttributeModifier.Operation.MULTIPLY_BASE));
		map.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(UUID.fromString("d59597ff-f6e8-4ab4-975d-bdb03fd60914"), "attack speed modif", i / 10f, EntityAttributeModifier.Operation.MULTIPLY_BASE));
		map.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(UUID.fromString("2e4526a7-ab89-4fb7-b788-7a065dd68532"), "attack damage modif", i / 10f, EntityAttributeModifier.Operation.MULTIPLY_BASE));
		map.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.fromString("2e4526a7-ab89-4fb7-b788-7a065dd68532"), "armor modif", i / 10f, EntityAttributeModifier.Operation.MULTIPLY_BASE));
		map.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(UUID.fromString("2e4526a7-ab89-4fb7-b788-7a065dd68532"), "toughness modif", i / 10f, EntityAttributeModifier.Operation.MULTIPLY_BASE));
		map.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(UUID.fromString("2e4526a7-ab89-4fb7-b788-7a065dd68532"), "knockback modif", i / 10f, EntityAttributeModifier.Operation.MULTIPLY_BASE));

		this.getAttributes().addTemporaryModifiers(map);

	}

	public int maxSize() {
		return hasSombrero() ? 10 : 5;
	}

	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}

	public boolean disablesShield() {
		return random.nextFloat() > 1 - (getSize() / 10f + 0.1);
	}

	public LaCucarachaEntity getEntity() {
		return this;
	}

	@Override
	protected void initGoals() {
		super.initGoals();

		this.targetSelector.add(0, new RevengeGoal(this, LivingEntity.class) {
			@Override
			public boolean canStart() {
				return super.canStart();
			}
		}.setGroupRevenge());

		this.goalSelector.add(1, new FleeEntityGoal<>(this, LivingEntity.class, 16.0F, 2, 3,
			livingEntity -> livingEntity.getHealth() > getHealth() || livingEntity instanceof PlayerEntity) {
			@Override
			public boolean canStart() {
				return getSize() < 5 && !isAggressive() && super.canStart();
			}
		});

		// priority 1
		// #TODO change to Grow/EatEntity Goal.
		this.goalSelector.add(1, new EatFoodGoal());

		this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, false, target ->
			!(target instanceof RoachEntity) && !(target.isSubmergedInWater())) {
			@Override
			public boolean canStart() {
				return isAggressive() && super.canStart();
			}
		});


		this.targetSelector.add(4, new ActiveTargetGoal<>(this, RoachEntity.class, true) {
			@Override
			public boolean canStart() {
				return isAggressive() && getMaxHealth() / 2 >= getHealth() && super.canStart();
			}
		});

		this.goalSelector.add(1, new PounceAttackGoal(this, 2) {
			@Override
			public boolean canStart() {
				return isAggressive() && isOnGround() && random.nextFloat() > 0.15f && super.canStart() && getEntityWorld().isSkyVisible(getBlockPos());
			}
		});


		this.goalSelector.add(3, new GroupTogetherGoal());


		this.goalSelector.add(5, new WanderAroundGoal(this, 1.8, 30, true) {
			@Nullable
			@Override
			protected Vec3d getWanderTarget() {
				return FuzzyTargeting.find(this.mob, 32, 12);
			}
		});


	}

	@Override
	public void attack(LivingEntity target, float pullProgress) {
		SpitEntity spitEntity = new SpitEntity(this.getWorld(), this);
		// this.dataTracker.set(NO_PROJECTILE, 40);

		if (this.getSize() >= 5) {
			for (int i = 0; i < 64; i++) {
				SpitEntity persistentProjectileEntity = new SpitEntity(this.getWorld(), this);
				double d = target.getX() - this.getX();
				double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
				double f = target.getZ() - this.getZ();
				double g = Math.sqrt(d * d + f * f);
				persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6F, (float) (16 - this.getWorld().getDifficulty().getId() * 2));
				this.getWorld().spawnEntity(persistentProjectileEntity);
			}
		} else {
			SpitEntity persistentProjectileEntity = new SpitEntity(this.getWorld(), this);
			double d = target.getX() - this.getX();
			double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
			double f = target.getZ() - this.getZ();
			double g = Math.sqrt(d * d + f * f);
			persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224, f, 2F, (float) (10 - this.getWorld().getDifficulty().getId() * 3));
			this.getWorld().spawnEntity(persistentProjectileEntity);
		}

		if (!this.isSilent()) {
			this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), LaCucaracha.LA_CUCARACHA_PUKE_SOUND_EVENT, this.getSoundCategory(), 5.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
		}


		this.getWorld().spawnEntity(spitEntity);
	}

	static class PounceAttackGoal extends Goal {
		private final LaCucarachaEntity cucaracha;
		private final float velocity;
		boolean cont = true;
		private LivingEntity target;

		public PounceAttackGoal(LaCucarachaEntity cucaracha, float velocity) {
			this.cucaracha = cucaracha;
			this.velocity = velocity;
			this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (this.cucaracha.hasPassengers()) {
				return false;
			} else {
				this.target = this.cucaracha.getTarget();
				if (this.target == null) {
					return false;
				} else {
					double d = this.cucaracha.squaredDistanceTo(this.target);
					if (d < 8.0 || d > 32.0) {
						return false;
					} else {
						return this.cucaracha.getRandom().nextInt(toGoalTicks(5)) == 0;
					}
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			return !this.cucaracha.isOnGround() && cont;
		}

		@Override
		public void start() {
			this.cucaracha.dataTracker.set(PROJECTILE_USAGE, 0); // Allows for jumping roaches to always spit facts
			Vec3d vec3d = this.cucaracha.getVelocity();
			Vec3d vec3d2 = new Vec3d(this.target.getX() - this.cucaracha.getX(), 0.0, this.target.getZ() - this.cucaracha.getZ());
			if (vec3d2.lengthSquared() > 1.0E-7) {
				vec3d2 = vec3d2.normalize().multiply(0.4).add(vec3d.multiply(0.2));
			}

			this.cucaracha.setVelocity(1.5F * vec3d2.x, this.velocity, 1.5F * vec3d2.z);
			cont = false;
		}

		@Override
		public void stop() {
			cont = false;
			super.stop();
		}
	}

	public static class CustomProjectileAttackGoal extends Goal {
		private final MobEntity mob;
		private final RangedAttackMob owner;
		private final double mobSpeed;
		private final int minIntervalTicks;
		private final int maxIntervalTicks;
		private final float maxShootRange;
		private final float squaredMaxShootRange;
		public int seenTargetTicks;
		@Nullable
		private LivingEntity target;
		private int updateCountdownTicks;

		public CustomProjectileAttackGoal(LaCucarachaEntity mob, double mobSpeed, int minIntervalTicks, int maxIntervalTicks, float maxShootRange) {
			this.updateCountdownTicks = -1;
			if (mob == null) {
				throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
			} else {
				this.owner = mob;
				this.mob = mob;
				this.mobSpeed = mobSpeed;
				this.minIntervalTicks = minIntervalTicks * (mob.getSize() >= 5 ? 5 : 1);
				this.maxIntervalTicks = maxIntervalTicks * (mob.getSize() >= 5 ? 5 : 1);
				this.maxShootRange = maxShootRange;
				this.squaredMaxShootRange = maxShootRange * maxShootRange;
				this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
			}
		}

		public int getSeenTargetTicks() {
			return seenTargetTicks;
		}

		public void setSeenTargetTicks(int i) {
			this.seenTargetTicks = i;
		}

		public boolean canStart() {
			LivingEntity livingEntity = this.mob.getTarget();
			if (livingEntity != null && livingEntity.isAlive()) {
				this.target = livingEntity;
				return true;
			} else {
				return false;
			}
		}

		public boolean shouldContinue() {
			return this.canStart() || this.target.isAlive() && !this.mob.getNavigation().isIdle() && this.seenTargetTicks < 100;
		}

		public void stop() {
			this.target = null;
			this.seenTargetTicks = 0;
			this.updateCountdownTicks = -1;
		}

		public boolean shouldRunEveryTick() {
			return true;
		}

		public void tick() {
			double d = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
			boolean bl = true;
			if (bl) {
				++this.seenTargetTicks;
			} else {
				this.seenTargetTicks = 0;
			}

			if (!(d > (double) this.squaredMaxShootRange) && this.seenTargetTicks >= 5) {
				this.mob.getNavigation().stop();
			} else {
				this.mob.getNavigation().startMovingTo(this.target, this.mobSpeed);
			}

			this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
			if (--this.updateCountdownTicks == 0) {
				if (!bl) {
					return;
				}

				float f = (float) Math.sqrt(d) / this.maxShootRange;
				float g = MathHelper.clamp(f, 0.1F, 1.0F);
				this.owner.attack(this.target, g);
				this.updateCountdownTicks = MathHelper.floor(f * (float) (this.maxIntervalTicks - this.minIntervalTicks) + (float) this.minIntervalTicks);
			} else if (this.updateCountdownTicks < 0) {
				this.updateCountdownTicks = MathHelper.floor(MathHelper.lerp(Math.sqrt(d) / (double) this.maxShootRange, (double) this.minIntervalTicks, (double) this.maxIntervalTicks));
			}

		}
	}

	class EatFoodGoal extends Goal {

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
				List<Entity> foods = getWorld().getOtherEntities(LaCucarachaEntity.this, getBoundingBox().expand(16),
					entity -> entity instanceof ItemEntity i && i.getStack().isFood() && i.isOnGround());
				foods.sort(Comparator.comparingDouble(e -> e.distanceTo(LaCucarachaEntity.this)));
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
				this.path = getNavigation().findPathTo(target, 32);
				if (path == null) {
					stop();
					return;
				}
				checkTicks = 20;
				getNavigation().startMovingTo(target.getX(), target.getY() + 1, target.getZ(), 1.5F);
			}
			if (distanceTo(target) < 2 + getSize()) {
				setTargetFoodEntityId(target.getId());
				stop();
			}
		}
	}

}
