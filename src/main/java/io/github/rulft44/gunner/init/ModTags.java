package io.github.rulft44.gunner.init;

import io.github.rulft44.gunner.Gunner;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
	public static class Items {
		public static final TagKey<Item> RECOIL_ENCHANTABLE = createTag("recoil_enchantable");
		public static final TagKey<Item> COLOR_BURST_ENCHANTABLE = createTag("color_burst_enchantable");

		private static TagKey<Item> createTag(String name) {
			return TagKey.of(RegistryKeys.ITEM, Identifier.of(Gunner.ID, name));
		}
	}
}
