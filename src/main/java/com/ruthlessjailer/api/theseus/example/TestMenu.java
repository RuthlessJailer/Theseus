package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.menu.Button;
import com.ruthlessjailer.api.theseus.menu.ButtonType;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * @author RuthlessJailer
 */
public final class TestMenu extends MenuBase {

	public TestMenu() {
		super(InventoryType.HOPPER, "&6Test Menu");
		addButton(1, new Button(new ItemStack(Material.BEDROCK), ButtonType.ACTION, (event, clicker, clickType, clicked) -> {
			clicker.closeInventory();
			clicker.setVelocity(new Vector(0, 10, 0));
			clicker.sendMessage("yeeted son");
		}));
	}

}
