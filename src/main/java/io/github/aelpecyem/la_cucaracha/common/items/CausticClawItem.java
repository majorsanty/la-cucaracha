package io.github.aelpecyem.la_cucaracha.common.items;

import io.github.aelpecyem.la_cucaracha.LaCucaracha;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;

public class CausticClawItem extends SwordItem {
	public CausticClawItem() {
		super(ToolMaterials.WOOD, 6, -1.5f, new FabricItemSettings());
	}


	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if(target.hasStatusEffect(LaCucaracha.CAUSTIC_FLUIDS) && target.getRandom().nextFloat() > 0.5f)
			target.addStatusEffect(new StatusEffectInstance(LaCucaracha.CAUSTIC_FLUIDS, 200,
				Math.min(3, target.getStatusEffect(LaCucaracha.CAUSTIC_FLUIDS).getAmplifier() + 1)));
		else
			target.addStatusEffect(new StatusEffectInstance(LaCucaracha.CAUSTIC_FLUIDS, 60, 0));

		return super.postHit(stack, target, attacker);
	}

}
