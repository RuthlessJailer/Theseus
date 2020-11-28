package com.ruthlessjailer.api.theseus.menu;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

/**
 * @author RuthlessJailer
 */
@Getter
public final class Button {

	private final ItemStack    item;
	private final ButtonAction action;

	public Button(@NonNull final ItemStack item, final ButtonAction action) {
		this.item   = item;
		this.action = action == null ? ButtonAction.EMPTY_ACTION : action;
	}
}
