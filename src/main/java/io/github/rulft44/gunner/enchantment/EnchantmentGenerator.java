package io.github.rulft44.gunner.enchantment;

import io.github.rulft44.gunner.enchantment.effect.RecoilEnchantmentEffect;
import io.github.rulft44.gunner.init.ModEnchantmentEffects;
import io.github.rulft44.gunner.init.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class EnchantmentGenerator extends FabricDynamicRegistryProvider {
	public EnchantmentGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
		// Our new enchantment, "Recoil."
		register(entries, ModEnchantmentEffects.RECOIL, Enchantment.builder(
					Enchantment.definition(
						registries.getOrThrow(RegistryKeys.ITEM).getOrThrow(ModTags.Items.RECOIL_ENCHANTABLE),
						// this is the "weight" or probability of our enchantment showing up in the table
						10,
						// the maximum level of the enchantment
						4,
						// base cost for level 1 of the enchantment, and min levels required for something higher
						Enchantment.leveledCost(1, 10),
						// same fields as above but for max cost
						Enchantment.leveledCost(1, 15),
						// anvil cost
						5,
						// valid slots
						AttributeModifierSlot.HAND
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.POST_ATTACK,
					EnchantmentEffectTarget.ATTACKER,
					EnchantmentEffectTarget.ATTACKER,
					new RecoilEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.4f, 0.2f)) // scale the enchantment linearly.
				)
		);
	}

	private void register(Entries entries, RegistryKey<Enchantment> key, Enchantment.Builder builder, ResourceCondition... resourceConditions) {
		entries.add(key, builder.build(key.getValue()), resourceConditions);
	}

	@Override
	public String getName() {
		return "GunnerEnchantmentGenerator";
	}
}
