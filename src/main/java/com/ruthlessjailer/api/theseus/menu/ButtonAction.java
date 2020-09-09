package com.ruthlessjailer.api.theseus.menu;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Vadim Hagedorn
 */
public interface ButtonAction {

	void onClick(@NonNull final Player clicker, @NonNull final ClickType clickType, final ItemStack clickedWith);

}
