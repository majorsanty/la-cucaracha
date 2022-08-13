package io.github.aelpecyem.la_cucaracha;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class SplashBottledRoachItem extends Item {
	public SplashBottledRoachItem() {
		super(new QuiltItemSettings().group(ItemGroup.BREWING).maxCount(1));
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
			 if (result instanceof EntityHitResult e && e.getEntity() instanceof LivingEntity l) {
			 	roach.setTarget(l);
			 }
			 world.spawnEntity(roach);
		}
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

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
