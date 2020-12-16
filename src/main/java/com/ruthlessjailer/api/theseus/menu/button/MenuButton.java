package com.ruthlessjailer.api.theseus.menu.button;

import com.ruthlessjailer.api.theseus.menu.MenuBase;
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
public class MenuButton extends ButtonBase {

	private MenuBase menu;

	public MenuButton(@NonNull final ItemStack item, @NonNull final MenuBase menu) {
		super(item);
		this.menu = menu;
	}

	@Override
	public void onClick(final @NonNull InventoryClickEvent event, final @NonNull Player clicker, final @NonNull ButtonBase clicked) {
		if (this.menu != null) {
			this.menu.displayTo(clicker);
		}
	}

}
