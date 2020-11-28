package com.ruthlessjailer.api.theseus.menu;

import com.ruthlessjailer.api.theseus.Checks;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.metadata.FixedMetadataValue;

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

		System.out.println("iClick Player");

		final Player player = (Player) event.getWhoClicked();

		final MenuBase menu = MenuBase.getCurrentMenu(player);

		if (menu == null) {
			return;
		}

		System.out.println("MenuClick");

		//now we know it's the right menu

		menu.onGenericClick(event);

		final Button clicked = menu.buttons.get(event.getSlot());

		if (clicked == null) {
			return;
		}

		System.out.println("ButtonClick");

		if (!clicked.getItem().isSimilar(event.getCurrentItem())) {
			return;
		}

		System.out.println("Success");

		//it's the right item

		event.setResult(Event.Result.DENY);
		clicked.getAction().onClick(event, (Player) event.getWhoClicked(), new Click(event.getClick()));

	}

	@EventHandler
	public void onClose(@NonNull final InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getPlayer();

		final MenuBase menu = MenuBase.getCurrentMenu(player);

		if (menu == null) {
			return;
		}

		menu.onClose(event);

		player.removeMetadata(MenuBase.NBT_CURRENT_MENU, Checks.instanceCheck());
		player.setMetadata(MenuBase.NBT_PREVIOUS_MENU, new FixedMetadataValue(Checks.instanceCheck(), menu));
	}


}
