package com.ruthlessjailer.api.theseus.menu.button;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author RuthlessJailer
 */
@Getter
@Setter
public abstract class ButtonBase implements Cloneable {

	private ItemStack item;
	private boolean   protect = true;

	public ButtonBase(@NonNull final ItemStack item) {
		this.item = item;
	}

	@Override
	@SneakyThrows
	public ButtonBase clone() {
		return (ButtonBase) super.clone();
	}

	public abstract void onClick(@NonNull final InventoryClickEvent event, @NonNull final Player clicker, @NonNull final ButtonBase clicked);
}
