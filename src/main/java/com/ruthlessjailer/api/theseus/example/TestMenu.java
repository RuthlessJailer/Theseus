package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import com.ruthlessjailer.api.theseus.menu.ListItem;
import com.ruthlessjailer.api.theseus.menu.ListMenu;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RuthlessJailer
 */
public class TestMenu extends ListMenu<ListItem> {

	public TestMenu() {
		super(54, "&3Test Menu");

		final List<ListItem> list = new ArrayList<>();

		for (final Material material : Material.values()) {
			if (material.isItem()) {
				list.add(new ListItem(null, ItemBuilder.of(material).name("An Item").build().create()));
			}
		}

		setAllItems(list);

		setExcludedSlots(0, 8);
//		setButton(0, new Button(new ItemStack(Material.BEDROCK), (event, clicker, clicked) -> {
//			if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
//
//				clicked.setItem(event.getCursor().clone());
//
//				if (event.getCursor().getAmount() > 1 && event.getClick().isRightClick()) {
//					event.getCursor().setAmount(event.getCursor().getAmount() - 1);
//				} else {
//					event.setCursor(null);
//				}
//
//				clicked.getItem().setAmount(1);
//
//				updateInventory();
//			} else {
//				clicker.getInventory().addItem(new ItemStack(Material.IRON_INGOT));
//			}
//		}));
//
//		setButton(8, new Button(new ItemStack(Material.SAND), (event, clicker, clicked) -> {
//			new YeetMenu().displayTo(clicker);
//		}));
//
//		setButton(4, new Button(new ItemStack(Material.GOLD_BLOCK), (event, clicker, clicked) -> {
//			if (clicked.getItem().getType() == Material.GOLD_BLOCK) {
//				clicked.setItem(new ItemStack(Material.DIAMOND_BLOCK));
//			} else {
//				clicked.setItem(new ItemStack(Material.GOLD_BLOCK));
//			}
//			updateInventory();
//		}));

	}

}
