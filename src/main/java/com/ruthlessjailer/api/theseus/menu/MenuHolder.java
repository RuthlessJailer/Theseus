package com.ruthlessjailer.api.theseus.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/**
 * @author Vadim Hagedorn
 */
@AllArgsConstructor
@Getter
public final class MenuHolder implements InventoryHolder {

	private final UUID uuid;

	public static boolean equals(final MenuHolder menu, final InventoryHolder holder) {
		return holder instanceof MenuHolder ? menu.getUuid().equals(((MenuHolder) holder).getUuid()) : menu.equals(holder);
	}

	@Override
	public Inventory getInventory() {
		return null;
	}
}
