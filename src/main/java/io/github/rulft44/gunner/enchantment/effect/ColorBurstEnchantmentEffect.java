package io.github.rulft44.gunner.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record ColorBurstEnchantmentEffect(EnchantmentLevelBasedValue amount) implements EnchantmentEntityEffect {
	public static final MapCodec<ColorBurstEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
			EnchantmentLevelBasedValue.CODEC.fieldOf("amount").forGetter(ColorBurstEnchantmentEffect::amount)
		).apply(instance, ColorBurstEnchantmentEffect::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		// The Enchantment Effect does not have functionality in here, it's in the ConfettiGunItem.java class.
	}

	@Override
	public MapCodec<? extends EnchantmentEntityEffect> getCodec() {
		return CODEC;
	}
}
