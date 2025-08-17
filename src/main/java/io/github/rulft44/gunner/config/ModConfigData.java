package io.github.rulft44.gunner.config;

import io.github.rulft44.gunner.Gunner;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Gunner.ID)
public class ModConfigData implements ConfigData{
	@ConfigEntry.Category("common")
	@ConfigEntry.Gui.CollapsibleObject()
	public GunSettings gunSettings = new GunSettings();

	@ConfigEntry.Category("common")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public GunSettings.EnchantSettings enchantSettings = new GunSettings.EnchantSettings();

	public static class GunSettings {
		public static class EnchantSettings {
			//@ConfigEntry.Gui.RequiresRestart
			@ConfigEntry.Gui.Tooltip
			public float recoilPower = 0.15F;

			@ConfigEntry.Gui.Tooltip
			public float fireworkPower = 3F;

			@ConfigEntry.Gui.Tooltip
			public int fireworkDuration = 1;
		}
	}
}
