package io.github.rulft44.gunner.init;

import com.mojang.serialization.MapCodec;
import io.github.rulft44.gunner.Gunner;
import io.github.rulft44.gunner.enchantment.effect.ColorBurstEnchantmentEffect;
import io.github.rulft44.gunner.enchantment.effect.RecoilEnchantmentEffect;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantmentEffects {
	public static final RegistryKey<Enchantment> RECOIL = of("recoil");
	public static MapCodec<RecoilEnchantmentEffect> RECOIL_EFFECT = register("recoil_effect", RecoilEnchantmentEffect.CODEC);

	public static final RegistryKey<Enchantment> COLOR_BURST = of("color_burst");
	public static MapCodec<ColorBurstEnchantmentEffect> COLOR_BURST_EFFECT = register("color_burst_effect", ColorBurstEnchantmentEffect.CODEC);

	private static RegistryKey<Enchantment> of(String path) {
		Identifier id = Identifier.of(Gunner.ID, path);
		return RegistryKey.of(RegistryKeys.ENCHANTMENT, id);
	}

	private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String id, MapCodec<T> codec) {
		return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(Gunner.ID, id), codec);
	}

	public static void initialize() {
		//Gunner.LOGGER.info("Registering EnchantmentEffects for" + Gunner.ID);
	}
}
