package io.github.rulft44.gunner.data;

import net.minecraft.entity.player.PlayerEntity;

import java.util.WeakHashMap;

public class PlayerGunData {
	private static final WeakHashMap<PlayerEntity, Boolean> usedGunFlag = new WeakHashMap<>();

	public static void markUsed(PlayerEntity player) {
		usedGunFlag.put(player, true);
	}

	public static boolean hasUsed(PlayerEntity player) {
		return usedGunFlag.getOrDefault(player, false);
	}

	public static void clear(PlayerEntity player) {
		usedGunFlag.remove(player);
	}
}
