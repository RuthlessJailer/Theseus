package com.ruthlessjailer.api.theseus.menu;

import com.ruthlessjailer.api.theseus.Checks;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * @author RuthlessJailer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class MenuListener implements Listener {

	private static final MenuListener instance = new MenuListener();

	static {
		Bukkit.getPluginManager().registerEvents(instance, Checks.instanceCheck("Unable to register MenuListener."));
	}

	@EventHandler
	public void onClick(@NonNull final InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getWhoClicked();

		final MenuBase menu = MenuBase.getCurrentMenu(player);

		if (menu == null) {
			return;
		}

		//now we know it's the right menu

		menu.onGenericClick(event);

		final Button clicked = menu.buttons.get(event.getSlot());

		if (clicked == null) {
			return;
		}

		if (!clicked.getItem().isSimilar(event.getCurrentItem())) {
			return;
		}

		//it's the right item

		switch (clicked.getType()) {
			case INFO:
				event.setCancelled(true);
				event.setCurrentItem(null);
				break;
			case TAKE:
				event.setCancelled(false);
				break;
			case ACTION:
				Checks.nullCheck(clicked.getAction(), "ButtonAction cannot be null as its type is ACTION!");
				clicked.getAction().onClick(event, (Player) event.getWhoClicked(), event.getClick(), event.getCurrentItem());
		}
	}

	@EventHandler
	public void onClose(@NonNull final InventoryCloseEvent event) {

	}

}
