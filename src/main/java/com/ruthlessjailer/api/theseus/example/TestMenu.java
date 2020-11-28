package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.menu.Button;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * @author RuthlessJailer
 */
public class TestMenu extends MenuBase {

	public TestMenu() {
		super(9 * 2, "&6Test Menu");
		addButton(0, new Button(new ItemStack(Material.BEDROCK), (event, clicker, click) -> {
			clicker.closeInventory();
			clicker.setVelocity(new Vector(0, -10, 0));
			clicker.sendMessage("yeeted son");
		}));

	}

}
