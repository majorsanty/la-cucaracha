package io.github.aelpecyem.la_cucaracha.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class RoachParticle extends SpriteBillboardParticle {

	private final SpriteProvider spriteProvider;

	protected RoachParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider,
							double velX, double velY, double velZ) {
		super(clientWorld, d, e, f);
		this.velocityX = velX;
		this.velocityY = velY;
		this.velocityZ = velZ;
		this.spriteProvider = spriteProvider;
		this.gravityStrength = 0.125F;
		setSpriteForAge(spriteProvider);
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.velocityY -= this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.setSpriteForAge(this.spriteProvider);
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {

		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z,
									   double velocityX, double velocityY, double velocityZ) {
			return new RoachParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ);
		}
	}
}
