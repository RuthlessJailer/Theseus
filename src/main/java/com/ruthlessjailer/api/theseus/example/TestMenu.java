package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.menu.Button;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author RuthlessJailer
 */
public class TestMenu extends MenuBase {

	public TestMenu() {
		super(9, "&3Test Menu");

		setButton(0, new Button(new ItemStack(Material.BEDROCK), (event, clicker, clicked) -> {
			if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {

				clicked.setItem(event.getCursor().clone());

				if (event.getCursor().getAmount() > 1 && event.getClick().isRightClick()) {
					event.getCursor().setAmount(event.getCursor().getAmount() - 1);
				} else {
					event.setCursor(null);
				}

				clicked.getItem().setAmount(1);

				updateInventory();
			} else {
				clicker.getInventory().addItem(new ItemStack(Material.IRON_INGOT));
			}
		}));

		setButton(8, new Button(new ItemStack(Material.SAND), (event, clicker, clicked) -> {
			new YeetMenu().displayTo(clicker);
		}));

		setButton(4, new Button(new ItemStack(Material.GOLD_BLOCK), (event, clicker, clicked) -> {
			if (clicked.getItem().getType() == Material.GOLD_BLOCK) {
				clicked.setItem(new ItemStack(Material.DIAMOND_BLOCK));
			} else {
				clicked.setItem(new ItemStack(Material.GOLD_BLOCK));
			}
			updateInventory();
		}));

	}

	public class YeetMenu extends MenuBase {

		public YeetMenu() {
			super(TestMenu.this, 9 * 1, "&6Yeet Menu");

			setButton(8, new Button(new ItemStack(Material.GLASS), (event, clicker, clicked) -> {
				new XDMenu().displayTo(clicker);
			}));
		}

		public class XDMenu extends MenuBase {

			public XDMenu() {
				super(YeetMenu.this, 9 * 1, "&cXD Menu");
			}

		}
	}

}
