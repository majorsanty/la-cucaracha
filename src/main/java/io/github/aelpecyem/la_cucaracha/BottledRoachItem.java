package io.github.aelpecyem.la_cucaracha;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BottledRoachItem extends Item {

	public BottledRoachItem() {
		super(new FabricItemSettings().group(ItemGroup.BREWING).maxCount(1).recipeRemainder(Items.GLASS_BOTTLE));
	}

	public static void writeRoachToTag(ItemStack bottle, RoachEntity entity) {
		if (!bottle.hasNbt()) {
			bottle.setNbt(new NbtCompound());
		}
		NbtCompound entityTag = new NbtCompound();
		entity.writeCustomDataToNbt(new NbtCompound());
		if (entity.hasCustomName()) {
			bottle.setCustomName(entity.getCustomName());
		}

		if (entity.isAiDisabled()) {
			entityTag.putBoolean("NoAI", entity.isAiDisabled());
		}

		if (entity.isSilent()) {
			entityTag.putBoolean("Silent", entity.isSilent());
		}

		if (entity.hasNoGravity()) {
			entityTag.putBoolean("NoGravity", entity.hasNoGravity());
		}

		if (entity.isGlowingLocal()) {
			entityTag.putBoolean("Glowing", entity.isGlowingLocal());
		}

		if (entity.isInvulnerable()) {
			entityTag.putBoolean("Invulnerable", entity.isInvulnerable());
		}

		entityTag.putFloat("Health", entity.getHealth());
		bottle.getNbt().put("EntityTag", entityTag);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient) {
			spawnRoach((ServerWorld) world, user.getStackInHand(hand), user, user.getBlockPos(), hand);
		}
		return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient) {
			spawnRoach((ServerWorld) context.getWorld(), context.getStack(), context.getPlayer(), context.getBlockPos(), context.getHand());
		}
		return ActionResult.SUCCESS;
	}

	private void spawnRoach(ServerWorld world, ItemStack stack, PlayerEntity player, BlockPos pos, Hand hand) {
		RoachEntity roach = (RoachEntity) LaCucaracha.ROACH_ENTITY_TYPE.spawnFromItemStack(world, stack, player, pos, SpawnReason.BUCKET, true, false);
		roach.setPersistent();
		player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, stack.getItem().getRecipeRemainder().getDefaultStack()));
	}
}
