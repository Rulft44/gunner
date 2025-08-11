package io.github.rulft44.gunner;

import io.github.rulft44.gunner.enchantment.EnchantmentGenerator;
import io.github.rulft44.gunner.tag.ModItemTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ModDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(EnchantmentGenerator::new);
		pack.addProvider(ModItemTagProvider::new);
	}
}
