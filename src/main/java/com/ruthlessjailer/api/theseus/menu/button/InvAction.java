package com.ruthlessjailer.api.theseus.menu.button;

import lombok.NonNull;
import org.bukkit.event.inventory.InventoryAction;

import static org.bukkit.event.inventory.InventoryAction.*;

/**
 * @author RuthlessJailer
 */
public enum InvAction {

	SWAP(SWAP_WITH_CURSOR, HOTBAR_SWAP),
	MOVE(MOVE_TO_OTHER_INVENTORY, HOTBAR_MOVE_AND_READD),
	COLLECT(COLLECT_TO_CURSOR),
	TAKE(PICKUP_ALL, PICKUP_HALF, PICKUP_ONE, PICKUP_SOME),
	PLACE(PLACE_ALL, PLACE_ONE, PLACE_SOME),
	DROP(DROP_ALL_CURSOR, DROP_ALL_SLOT, DROP_ONE_CURSOR, DROP_ONE_SLOT),
	CREATIVE(CLONE_STACK),
	NONE(NOTHING);

	final InventoryAction[] actions;

	InvAction(final InventoryAction... actions) {
		this.actions = actions;
	}

	public boolean is(@NonNull final InventoryAction action) {
		for (final InventoryAction a : this.actions) {
			if (a == action) {
				return true;
			}
		}

		return false;
	}

}
