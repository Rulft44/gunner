package io.github.rulft44.gunner.item;

import dev.doublekekse.confetti.Confetti;
import dev.doublekekse.confetti.math.Vec3Dist;
import dev.doublekekse.confetti.packet.ExtendedParticlePacket;
import io.github.rulft44.gunner.init.ModEnchantmentEffects;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ConfettiGunItem extends Item {
	public ConfettiGunItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		var forward = user.getRotationVec(1);

		if (!world.isClient()) {
			var right = forward.crossProduct(new Vec3d(0, 1.7, 0));

			var pos = user.getPos()
				.add(0, 1, 0)
				.add(right.multiply(.3))
				.add(forward.multiply(.5));

			world.getPlayers().forEach(serverPlayer -> {
				ServerPlayNetworking.send((ServerPlayerEntity) serverPlayer, new ExtendedParticlePacket(new Vec3Dist(pos, 0), new Vec3Dist(forward.multiply(0.8), new Vec3d(.25, .25, .25)), 70, false, Confetti.CONFETTI));
			});

			if (!user.isSneaking() && hasEnchantment(user.getStackInHand(hand), ModEnchantmentEffects.RECOIL)) {
				int level = getLevel(user.getStackInHand(hand), ModEnchantmentEffects.RECOIL);
				float pitch = user.getPitch(1);
				float pitchFactor = (pitch + 90F) / 180F;
				float recoilAmount = level * 0.25F * pitchFactor;
				Vec3d backward = forward.multiply(-recoilAmount);
				double upwardBoost = 0.1 * pitchFactor * level;
				Vec3d finalRecoil = new Vec3d(backward.x, backward.y + upwardBoost, backward.z);

				user.addVelocity(finalRecoil.x, finalRecoil.y, finalRecoil.z);
				user.velocityModified = true;
			}
		}

		return ActionResult.SUCCESS;
	}

	public static boolean hasEnchantment(ItemStack stack, RegistryKey<Enchantment> enchantment) {
		return stack.getEnchantments().getEnchantments().toString().
			contains(enchantment.getValue().toString());
	}

	public static int getLevel(ItemStack stack, RegistryKey<Enchantment> enchantment){
		for (RegistryEntry<Enchantment> enchantments : stack.getEnchantments().getEnchantments()){
			if (enchantments.toString().contains(enchantment.getValue().toString())){
				return stack.getEnchantments().getLevel(enchantments);
			}
		}
		return 0;
	}
}
