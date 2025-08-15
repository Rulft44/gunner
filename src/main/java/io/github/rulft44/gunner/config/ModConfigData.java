package io.github.rulft44.gunner.config;

import io.github.rulft44.gunner.Gunner;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Gunner.ID)
public class ModConfigData implements ConfigData{
	@ConfigEntry.Category("common")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public GunSettings gunSettings = new GunSettings();

	public static class GunSettings {

		//@ConfigEntry.Gui.RequiresRestart
		@ConfigEntry.Gui.Tooltip
		public float recoilPower = 0.15f;
	}
}
