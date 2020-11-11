package com.ruthlessjailer.api.theseus.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * @author RuthlessJailer
 */
@AllArgsConstructor
@Getter
public abstract class ButtonBase {

	private final ItemStack    item;
	private final ButtonType   type;
	private final ButtonAction action;

}
