package com.ruthlessjailer.api.theseus.menu;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

/**
 * @author RuthlessJailer
 */
@Getter
@Setter
public class Button {

	private ItemStack    item;
	private ButtonAction action;

	public Button(@NonNull final ItemStack item) {
		this(item, ButtonAction.EMPTY_ACTION);
	}

	public Button(@NonNull final ItemStack item, final ButtonAction action) {
		this.item   = item;
		this.action = action == null ? ButtonAction.EMPTY_ACTION : action;
	}
}
