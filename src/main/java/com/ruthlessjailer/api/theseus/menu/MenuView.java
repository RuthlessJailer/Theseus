package com.ruthlessjailer.api.theseus.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

/**
 * @author RuthlessJailer
 */
@Getter
@AllArgsConstructor
public final class MenuView extends InventoryView {

	private final Inventory topInventory, bottomInventory;
	private final String      title;
	private final HumanEntity player;

	@Override
	public InventoryType getType() {
		return this.topInventory.getType();
	}
}
