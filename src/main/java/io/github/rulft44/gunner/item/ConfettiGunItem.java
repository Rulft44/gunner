package io.github.rulft44.gunner.item;

import dev.doublekekse.confetti.Confetti;
import dev.doublekekse.confetti.math.Vec3Dist;
import dev.doublekekse.confetti.packet.ExtendedParticlePacket;
import dev.doublekekse.confetti.particle.ConfettiOptions;
import io.github.rulft44.gunner.Gunner;
import io.github.rulft44.gunner.init.ModEnchantmentEffects;
import io.github.rulft44.gunner.render.item.ConfettiGunGeoItemRenderer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
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

public class ConfettiGunItem extends Item implements GeoItem{
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
	public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
		if (entity instanceof PlayerEntity player) {
			boolean holding = player.getMainHandStack() == stack || player.getOffHandStack() == stack;

			var attrInstance = player.getAttributeInstance(EntityAttributes.SAFE_FALL_DISTANCE);
			if (attrInstance != null) {
				if (holding) {
					attrInstance.setBaseValue(100);
				} else if (attrInstance.getBaseValue() == 100) {
					attrInstance.setBaseValue(0);
				}
			}
		}
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		var forward = user.getRotationVec(1);
		ItemStack itemStack = user.getStackInHand(hand);

		// Play Sound
		user.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, 2f, 0.7f);

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
			triggerAnim(user, GeoItem.getOrAssignId(itemStack, serverWorld), "fire_controller", "fire");

			if (hasEnchantment(itemStack, ModEnchantmentEffects.COLOR_BURST) && consumeAmmo(user, Items.FIREWORK_ROCKET)) {
				Vec3d spawnPos = user.getEyePos().add(forward.multiply(0.5));
				ItemStack firework = new ItemStack(Items.FIREWORK_ROCKET);

				// Generate multiple confetti colors
				int[] colors = generateConfettiColors(5);

				// Define an explosion
				FireworkExplosionComponent explosion = new FireworkExplosionComponent(
					FireworkExplosionComponent.Type.SMALL_BALL,
					new it.unimi.dsi.fastutil.ints.IntArrayList(colors),
					it.unimi.dsi.fastutil.ints.IntList.of(),
					true,
					true
				);

				// Attach the explosion to the firework item
				firework.set(DataComponentTypes.FIREWORKS,
					new net.minecraft.component.type.FireworksComponent(
						Gunner.config.enchantSettings.fireworkDuration,
						java.util.List.of(explosion)
					)
				);

				// Spawn rocket with firework stack
				FireworkRocketEntity rocket = new FireworkRocketEntity(world, firework, spawnPos.x, spawnPos.y, spawnPos.z, true);
				rocket.setVelocity(forward.x, forward.y, forward.z, Gunner.config.enchantSettings.fireworkPower, 0.0F);
				rocket.setOwner(user);
				world.spawnEntity(rocket);
			}

			if (!user.isSneaking()) {
				if(hasEnchantment(itemStack, ModEnchantmentEffects.RECOIL)){
					int level = getLevel(itemStack, ModEnchantmentEffects.RECOIL);
					float pitch = user.getPitch(1);
					float pitchFactor = (pitch + 90F) / 180F;
					float recoilAmount = level * Gunner.config.enchantSettings.recoilPower * pitchFactor;
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

	private static boolean consumeAmmo(PlayerEntity player, Item item) {
		if (player.isCreative()) {
			return true;
		}

		var inv = player.getInventory();
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getStack(i);
			if (!stack.isEmpty() && stack.isOf(item)) {
				stack.decrement(1);
				return true;
			}
		}

		return false;
	}

	private static int[] generateConfettiColors(int count) {
		ConfettiOptions options = new ConfettiOptions.Builder().build();
		int[] colors = new int[count];

		for (int i = 0; i < count; i++) {
			float[] rgb = options.colorSupplier().get();
			int r = (int)(rgb[0] * 255);
			int g = (int)(rgb[1] * 255);
			int b = (int)(rgb[2] * 255);
			colors[i] = (r << 16) | (g << 8) | b;
		}

		return colors;
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
