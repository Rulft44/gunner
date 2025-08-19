package io.github.rulft44.gunner.init;

import io.github.rulft44.gunner.Gunner;
import io.github.rulft44.gunner.item.ConfettiGunItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

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

	public static final Item CONFETTI_GUN = register("confetti_gun", ConfettiGunItem::new, new Item.Settings().maxCount(1).enchantable(2).useCooldown(1f));
	public static final Item GUN_COMPONENT = register("gun_component", Item::new, new Item.Settings().maxCount(16));

	public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Gunner.ID, "item_group"));
	public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
		.icon(() -> new ItemStack(ModItems.CONFETTI_GUN))
		.displayName(Text.translatable("itemGroup.gunner"))
		.entries((context, entries) -> {
			entries.add(new ItemStack(ModItems.CONFETTI_GUN));
			entries.add(new ItemStack(ModItems.GUN_COMPONENT));
			addEnchantedBooks(context, entries);
		})
		.build();

	private static void addEnchantedBooks(ItemGroup.DisplayContext context, ItemGroup.Entries entries) {
		ItemStack recoilBook = new ItemStack(Items.ENCHANTED_BOOK);
		ItemStack colorBurstBook = new ItemStack(Items.ENCHANTED_BOOK);
		context.lookup()
			.getOrThrow(RegistryKeys.ENCHANTMENT)
			.getOptional(ModEnchantmentEffects.RECOIL)
			.ifPresent(recoil -> recoilBook.addEnchantment(recoil, 4));
		context.lookup()
			.getOrThrow(RegistryKeys.ENCHANTMENT)
			.getOptional(ModEnchantmentEffects.COLOR_BURST)
			.ifPresent(colorBurst -> colorBurstBook.addEnchantment(colorBurst, 1));
		entries.add(recoilBook);
		entries.add(colorBurstBook);
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
			.register((itemGroup) -> itemGroup.add(ModItems.CONFETTI_GUN));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
			.register((itemGroup) -> itemGroup.add(ModItems.GUN_COMPONENT));

		// Register the group.
		Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);
	}

	private ModItems() {
	}
}
