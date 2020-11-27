package com.ruthlessjailer.api.theseus.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * @author RuthlessJailer
 */
@AllArgsConstructor
@Getter
public final class Button {

	private final ItemStack    item;
	private final ButtonType   type;
	private final ButtonAction action;

}
