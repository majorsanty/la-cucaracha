package io.github.aelpecyem.la_cucaracha;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class SplashBottledRoachItem extends Item {

	public SplashBottledRoachItem() {
		super(new QuiltItemSettings().group(ItemGroup.BREWING).maxCount(1));
		DispenserBlock.registerBehavior(this, new DispenserBehavior() {
			@Override
			public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
				return (new ProjectileDispenserBehavior() {
					@Override
					protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
						return Util.make(new SplashBottledRoachEntity(world, position.getX(), position.getY(), position.getZ()), entity -> entity.setItem(stack));
					}

					@Override
					protected float getVariation() {
						return super.getVariation() * 0.5F;
					}

					@Override
					protected float getForce() {
						return super.getForce() * 1.25F;
					}
				}).dispense(blockPointer, itemStack);
			}
		});
	}

	public static void onHit(World world, HitResult result) {
		Vec3d pos = result.getPos();
		RandomGenerator random = world.getRandom();
		int amount = 3 + random.nextInt(3);
		for (int i = 0; i < amount; i++) {
			RoachEntity roach = new RoachEntity(LaCucaracha.ROACH_ENTITY_TYPE, world);
			roach.updatePositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), random.nextInt(360), 0);
			roach.setVelocity(MathHelper.nextFloat(random, 0.07F, 0.1F),
							  MathHelper.nextFloat(random, 0.3F, 0.5F),
							  MathHelper.nextFloat(random, 0.07F, 0.1F));
			roach.setSummoned(true);
			roach.initialize((ServerWorldAccess) world, world.getLocalDifficulty(roach.getBlockPos()), SpawnReason.MOB_SUMMONED, null, null);
			LivingEntity target;
			if (result instanceof EntityHitResult e && e.getEntity() instanceof LivingEntity l && !(e.getEntity() instanceof RoachEntity)) {
				target = l;
			} else {
				target = world.getClosestEntity(LivingEntity.class, TargetPredicate.createAttackable()
					.setPredicate(living -> !(living instanceof RoachEntity)),
									   roach, pos.getX(), pos.getY(), pos.getZ(), roach.getBoundingBox().expand(1));
			}
			if (target != null) {
				roach.setTarget(target);
				roach.setSpecificTarget(target.getUuid());
			}
			world.spawnEntity(roach);
		}
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F,
						0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

		if (!world.isClient) {
			SplashBottledRoachEntity entity = new SplashBottledRoachEntity(world, user);
			entity.setItem(itemStack);
			entity.setProperties(user, user.getPitch(), user.getYaw(), -20.0F, 0.5F, 1.0F);
			world.spawnEntity(entity);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.getAbilities().creativeMode) {
			itemStack.decrement(1);
		}

		return TypedActionResult.success(itemStack, world.isClient());
	}
}
