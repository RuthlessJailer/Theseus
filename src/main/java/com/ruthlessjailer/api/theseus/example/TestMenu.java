package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import com.ruthlessjailer.api.theseus.menu.Button;
import com.ruthlessjailer.api.theseus.menu.ListItem;
import com.ruthlessjailer.api.theseus.menu.ListMenu;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author RuthlessJailer
 */
public class TestMenu extends MenuBase {

	public TestMenu() {
		super(MIN_SLOTS, "&3Test Menu");

		setButton(0, new Button(ItemBuilder.of(Material.BEDROCK, "&eCUSTOMIZABLE ITEM").build().create(), (event, clicker, clicked) -> {
			if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {

				clicked.setItem(ItemBuilder.of(event.getCursor()).name("&cCUSTOMIZABLE ITEM").build().create());

				if (event.getCursor().getAmount() > 1 && event.getClick().isRightClick()) {
					event.getCursor().setAmount(event.getCursor().getAmount() - 1);
				} else {
					event.setCursor(null);
				}

				clicked.getItem().setAmount(1);

				updateInventory();
			} else {
				clicker.getInventory().addItem(ItemBuilder.of(Material.IRON_INGOT, "&cingot").build().create());
			}
		}));

		final YeetMenu yeet = new YeetMenu();

		setButton(8, new Button(ItemBuilder.of(Material.SAND, "&2YEET MENU").build().create(), (event, clicker, clicked) -> {
			yeet.displayTo(clicker);
		}));

		final ItemStack gold    = ItemBuilder.of(Material.GOLD_BLOCK, "&6GOLD").build().create();
		final ItemStack diamond = ItemBuilder.of(Material.DIAMOND_BLOCK, "&9DIAMOND").build().create();

		setButton(4, new Button(gold, (event, clicker, clicked) -> {
			if (clicked.getItem().isSimilar(gold)) {
				clicked.setItem(diamond);
			} else {
				clicked.setItem(gold);
			}
			updateInventory();
		}));

	}

	public class YeetMenu extends ListMenu<ListItem> {

		public YeetMenu() {
			super(TestMenu.this, MAX_SLOTS, "&3Yeet Menu &a" + CURRENT_PAGE_PLACEHOLDER + "&8/&9" + TOTAL_PAGES_PLACEHOLDER);

			final List<ListItem> list  = new ArrayList<>();
			final List<Material> items = Arrays.stream(Material.values()).filter(Material::isItem).collect(Collectors.toList());

			for (int i = 0; i < 200; i++) {
				list.add(new ListItem(i, ItemBuilder.of(Common.selectRandom(items)).build().create(),
									  (event, clicker, clicked) -> {
										  event.setCancelled(false);
										  event.setResult(Event.Result.ALLOW);
									  }));
			}

			System.out.println(list.size());

			setExcludedSlots(0, 8, 45, 53);

			setBackButtonSlot(45);
			setNextButtonSlot(53);
			setPreviousMenuButtonSlot(0);//back button will null previous one

			setButton(8, new Button(ItemBuilder.of(Material.POTATO).name("&5yeet").build().create(), (event, clicker, clicked) -> {
				clicker.closeInventory();
				clicker.setVelocity(new Vector(0, 10, 0));
				Chat.send(clicker, "&dyeeted");
			}));

			setAllItems(list);
		}
	}

}
