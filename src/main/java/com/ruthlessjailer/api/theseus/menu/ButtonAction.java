package com.ruthlessjailer.api.theseus.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.Serializable;

/**
 * @author RuthlessJailer
 */
public interface ButtonAction extends Serializable {

	ButtonAction EMPTY_ACTION = (event, clicker, click) -> {};

	void onClick(final InventoryClickEvent event, final Player clicker, final Click click);

}
