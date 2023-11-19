package io.github.aelpecyem.la_cucaracha;

import io.github.aelpecyem.la_cucaracha.items.SplashBottledRoachItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SplashBottledRoachEntity extends ThrownItemEntity {

	public SplashBottledRoachEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
		super(entityType, world);
	}

	public SplashBottledRoachEntity(World world, LivingEntity livingEntity) {
		super(LaCucaracha.SPLASH_BOTTLED_ROACH_ENTITY_TYPE, livingEntity, world);
	}

	public SplashBottledRoachEntity(World world, double x, double y, double z) {
		super(LaCucaracha.SPLASH_BOTTLED_ROACH_ENTITY_TYPE, x, y, z, world);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getWorld().isClient) {
			getWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1, 1);
			LaCucaracha.sendRoachPotionPacket(this);
			SplashBottledRoachItem.onHit(getWorld(), hitResult);
			this.discard();
		}
	}

	@Override
	protected float getGravity() {
		return 0.05F;
	}

	@Override
	protected Item getDefaultItem() {
		return LaCucaracha.SPLASH_POTION_ROACH_ITEM;
	}
}
