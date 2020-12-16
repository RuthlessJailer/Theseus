package com.ruthlessjailer.api.theseus.menu.button;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author RuthlessJailer
 */
@Getter
@Setter
public class ActionButton extends ButtonBase {

	private ButtonAction action;

	public ActionButton(@NonNull final ItemStack item) {
		this(item, ButtonAction.EMPTY_ACTION);
	}

	@Override
	public void onClick(final @NonNull InventoryClickEvent event, final @NonNull Player clicker, final @NonNull ButtonBase clicked) {
		if (this.action != null) {
			this.action.onClick(event, clicker, clicked);
		}
	}

	public ActionButton(@NonNull final ItemStack item, final ButtonAction action) {
		super(item);
		this.action = action == null ? ButtonAction.EMPTY_ACTION : action;
	}
}
