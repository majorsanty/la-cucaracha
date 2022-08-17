package io.github.aelpecyem.la_cucaracha.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class LaCucarachaClient implements ClientModInitializer {

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient(ModContainer mod) {
		EntityRendererRegistry.register(LaCucaracha.ROACH_ENTITY_TYPE, RoachEntityRenderer::new);
		EntityRendererRegistry.register(LaCucaracha.SPLASH_BOTTLED_ROACH_ENTITY_TYPE, FlyingItemEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(RoachEntityModel.LAYER_LOCATION, RoachEntityModel::createTexturedModelData);

		ClientPlayNetworking.registerGlobalReceiver(LaCucaracha.PARTICLE_PACKET, (client, handler, buf, responseSender) -> {
			Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
			World world = client.world;
			if (world != null) {
				client.execute(() -> {
					for (int i = 0; i < 8; i++) {
						client.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, Items.SPLASH_POTION.getDefaultStack()),
												 pos.getX(), pos.getY(), pos.getZ(),
												 world.random.nextGaussian() * 0.1,
												 world.random.nextGaussian() * 0.1,
												 world.random.nextGaussian() * 0.1);
						client.world.addParticle(LaCucaracha.ROACH_PARTICLE_EFFECT,
												 pos.getX(), pos.getY(), pos.getZ(),
												 world.random.nextGaussian() * 0.1,
												 world.random.nextFloat(),
												 world.random.nextGaussian() * 0.1);
					}
				});
			}
		});

		ParticleFactoryRegistry.getInstance().register(LaCucaracha.ROACH_PARTICLE_EFFECT, RoachParticle.Factory::new);
	}

}
