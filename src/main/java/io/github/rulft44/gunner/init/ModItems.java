package io.github.rulft44.gunner.init;

import io.github.rulft44.gunner.Gunner;
import io.github.rulft44.gunner.item.ConfettiGunItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
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

	public static final Item CONFETTI_GUN = register("confetti_gun", ConfettiGunItem::new, new Item.Settings().maxCount(1).enchantable(2).useCooldown(1f)
		.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.SAFE_FALL_DISTANCE,
			new EntityAttributeModifier(Identifier.of("safe_fall_distance"),
			100, EntityAttributeModifier.Operation.ADD_VALUE),
			AttributeModifierSlot.HAND).build()));
	public static final Item GUN_COMPONENT = register("gun_component", Item::new, new Item.Settings().maxCount(16));

	public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Gunner.ID, "item_group"));
	public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
		.icon(() -> new ItemStack(ModItems.CONFETTI_GUN))
		.displayName(Text.translatable("itemGroup.gunner"))
		//.texture(Identifier.of(Gunner.ID, "textures/item/gun_component"))
		.build();

	public static void initialize() {
			ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
			.register((itemGroup) -> itemGroup.add(ModItems.CONFETTI_GUN));
			ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
			.register((itemGroup) -> itemGroup.add(ModItems.GUN_COMPONENT));

		// Register the group.
		Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);

		// Register items to the custom item group.
		ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register(itemGroup -> {
			itemGroup.add(ModItems.CONFETTI_GUN);
			itemGroup.add(ModItems.GUN_COMPONENT);
		});
	}
}
