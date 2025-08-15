package io.github.rulft44.gunner.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class ModConfig implements ConfigData {
	public static ModConfigData getConfig() {
		return AutoConfig.getConfigHolder(ModConfigData.class).getConfig();
	}

	public static void saveConfig() {
		AutoConfig.getConfigHolder(ModConfigData.class).save();
	}

	public static void register() {
		AutoConfig.register(ModConfigData.class, JanksonConfigSerializer::new);
	}
}
