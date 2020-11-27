package com.ruthlessjailer.api.theseus.menu;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author RuthlessJailer
 */
public interface ButtonAction {

	void onClick(@NonNull final InventoryClickEvent event, @NonNull final Player clicker, @NonNull final ClickType clickType, final ItemStack clicked);

}
