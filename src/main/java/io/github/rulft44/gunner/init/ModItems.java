package io.github.rulft44.gunner.init;

import io.github.rulft44.gunner.Gunner;
import io.github.rulft44.gunner.item.ConfettiGunItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
	public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		// Create the item key.
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Gunner.ID, name));

		// Create the item instance.
		Item item = itemFactory.apply(settings.registryKey(itemKey));

		// Register the item.
		Registry.register(Registries.ITEM, itemKey, item);

		return item;
	}

	public static final Item CONFETTI_GUN = register("confetti_gun", ConfettiGunItem::new, new Item.Settings().maxCount(1).enchantable(2).useCooldown(0.5f));

	public static void initialize() {
			ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
			.register((itemGroup) -> itemGroup.add(ModItems.CONFETTI_GUN));
	}
}
