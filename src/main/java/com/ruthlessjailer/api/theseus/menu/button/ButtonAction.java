package com.ruthlessjailer.api.theseus.menu.button;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.Serializable;

/**
 * @author RuthlessJailer
 */
public interface ButtonAction extends Serializable {

	ButtonAction EMPTY_ACTION = (event, clicker, clicked) -> {};

	void onClick(@NonNull final InventoryClickEvent event, @NonNull final Player clicker, @NonNull final ButtonBase clicked);

}
