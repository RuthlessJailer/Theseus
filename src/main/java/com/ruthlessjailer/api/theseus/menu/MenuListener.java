package com.ruthlessjailer.api.theseus.menu;

import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * @author Vadim Hagedorn
 */

public final class MenuListener implements Listener {

	private MenuListener() {
		Bukkit.getPluginManager().registerEvents(this, Checks.instanceCheck(String.format(
				"Plugin instance cannot be null when initializing menu listener %s.",
				ReflectUtil.getPath(this.getClass()))));
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {

	}

}
