package io.github.aelpecyem.la_cucaracha.common.entity;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import io.github.aelpecyem.la_cucaracha.client.LaCucarachaClient;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpitEntity extends ProjectileEntity {


	public SpitEntity(World world, LivingEntity owner) {
		this(LaCucaracha.SPIT_ENTITY_TYPE, world);
		this.setOwner(owner);
		this.setPosition(owner.getX() - (double) (owner.getWidth() + 1.0F) * 0.5 * (double) MathHelper.sin(owner.bodyYaw * 0.017453292F), owner.getEyeY() - 0.10000000149011612, owner.getZ() + (double) (owner.getWidth() + 1.0F) * 0.5 * (double) MathHelper.cos(owner.bodyYaw * 0.017453292F));
	}

	public SpitEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
		super(entityType, world);
	}


	public void tick() {
		super.tick();
		Vec3d vec3d = this.getVelocity();
		HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
		this.onCollision(hitResult);
		double d = this.getX() + vec3d.x;
		double e = this.getY() + vec3d.y;
		double f = this.getZ() + vec3d.z;
		this.updateRotation();
		float g = 0.99F;
		float h = 0.06F;
		if (this.getWorld().getStatesInBox(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
			this.discard();
		} else if (this.isInsideWaterOrBubbleColumn()) {
			this.discard();
		} else {
			this.setVelocity(vec3d.multiply(0.9900000095367432));
			if (!this.hasNoGravity()) {
				this.setVelocity(this.getVelocity().add(0.0, -0.05999999865889549, 0.0));
			}

			this.setPosition(d, e, f);
		}

	}

	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		Entity var3 = this.getOwner();
		if (var3 instanceof LivingEntity livingEntity) {
			entityHitResult.getEntity().damage(this.getDamageSources().mobProjectile(this, livingEntity), 12.0F);
			if (entityHitResult.getEntity() instanceof LivingEntity e) {
				if(e.hasStatusEffect(LaCucaracha.CAUSTIC_FLUIDS))
					e.addStatusEffect(new StatusEffectInstance(LaCucaracha.CAUSTIC_FLUIDS, 100,
						Math.min(3, e.getStatusEffect(LaCucaracha.CAUSTIC_FLUIDS).getAmplifier() + 1)));
				else
					e.addStatusEffect(new StatusEffectInstance(LaCucaracha.CAUSTIC_FLUIDS, 60, 0));
			}
		}

	}

	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		if (!this.getWorld().isClient) {
			this.discard();
		}

	}

	protected void initDataTracker() {
	}

	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		double d = packet.getVelocityX();
		double e = packet.getVelocityY();
		double f = packet.getVelocityZ();

		for (int i = 0; i < 7; ++i) {
			double g = 0.4 + 0.1 * (double) i;
			this.getWorld().addParticle(LaCucarachaClient.AWFUL_SPIT_PARTICLE_EFFECT, this.getX(), this.getY(), this.getZ(), d * g, e, f * g);
		}

		this.setVelocity(d, e, f);
	}
}
