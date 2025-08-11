package io.github.rulft44.gunner;

import io.github.rulft44.gunner.init.ModEnchantmentEffects;
import io.github.rulft44.gunner.init.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gunner implements ModInitializer {
	public static final String ID = "gunner";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Confetti Gunner] skibidi dop dop dop yes yes yes (ts so buns :wilted_rose:) https://discord.com/channels/674795434509598731/1399068756050841641/1404430386171478188");

		ModItems.initialize();
		ModEnchantmentEffects.initialize();
	}
}
