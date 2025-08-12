package io.github.rulft44.gunner.item;

import dev.doublekekse.confetti.Confetti;
import dev.doublekekse.confetti.math.Vec3Dist;
import dev.doublekekse.confetti.packet.ExtendedParticlePacket;
import io.github.rulft44.gunner.init.ModEnchantmentEffects;
import io.github.rulft44.gunner.render.item.ConfettiGunGeoItemRenderer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ConfettiGunItem extends Item implements GeoItem {
	private static final RawAnimation FIRE_ANIM = RawAnimation.begin().thenPlay("fire");
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public ConfettiGunItem(Settings settings) {
		super(settings);

		GeoItem.registerSyncedAnimatable(this);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 0;
	}

	@Override
	public boolean isUsedOnRelease(ItemStack stack) {
		return false;
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		var forward = user.getRotationVec(1);

		if (world instanceof ServerWorld serverWorld) {
			var right = forward.crossProduct(new Vec3d(0, 1.7, 0));

			var pos = user.getPos()
				.add(0, 1, 0)
				.add(right.multiply(.3))
				.add(forward.multiply(.5));

			// Send Confetti Particles
			world.getPlayers().forEach(serverPlayer -> {
				ServerPlayNetworking.send((ServerPlayerEntity) serverPlayer, new ExtendedParticlePacket(new Vec3Dist(pos, 0), new Vec3Dist(forward.multiply(0.8), new Vec3d(.25, .25, .25)), 70, false, Confetti.CONFETTI));
			});

			// Trigger Gun Spinning Animation
			triggerAnim(user, GeoItem.getOrAssignId(user.getStackInHand(hand), serverWorld), "fire_controller", "fire");

			if (!user.isSneaking()) {
				if(hasEnchantment(user.getStackInHand(hand), ModEnchantmentEffects.RECOIL)){
					int level = getLevel(user.getStackInHand(hand), ModEnchantmentEffects.RECOIL);
					float pitch = user.getPitch(1);
					float pitchFactor = (pitch + 90F) / 180F;
					float recoilAmount = level * 0.15F * pitchFactor;
					Vec3d backward = forward.multiply(-recoilAmount);
					double upwardBoost = 0.15 * pitchFactor * level;
					Vec3d finalRecoil = new Vec3d(backward.x, backward.y + upwardBoost, backward.z);

					// Apply Recoil
					user.addVelocity(finalRecoil.x, finalRecoil.y, finalRecoil.z);
					user.velocityModified = true;
				}
			}
		}

		return ActionResult.CONSUME;
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

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>("fire_controller", 0, animTest -> PlayState.STOP)
			.triggerableAnim("fire", FIRE_ANIM));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private ConfettiGunGeoItemRenderer renderer;

			@Override
			public @Nullable GeoItemRenderer<ConfettiGunItem> getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new ConfettiGunGeoItemRenderer();

				return this.renderer;
			}
		});
	}
}
