package io.github.aelpecyem.la_cucaracha.common;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CorrosiveFluidsStatusEffect extends StatusEffect {
	public CorrosiveFluidsStatusEffect(StatusEffectCategory category, int color) {
		super(category, color);
	}

	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (entity.age % 20 == 0)
			entity.damage(LaCucaracha.create(LaCucaracha.ROACH, entity.getWorld()), 1.0F);
	}
}
