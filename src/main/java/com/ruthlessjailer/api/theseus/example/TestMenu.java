package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import com.ruthlessjailer.api.theseus.menu.Button;
import com.ruthlessjailer.api.theseus.menu.ListItem;
import com.ruthlessjailer.api.theseus.menu.ListMenu;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import com.ruthlessjailer.api.theseus.multiversion.XMaterial;
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

			for (final Material material : items) {
				list.add(new ListItem(material, ItemBuilder.of(material).build().create(),
									  (event, clicker, clicked) -> {
										  event.setCancelled(false);
										  event.setResult(Event.Result.ALLOW);
									  }));
			}

			final int[] excluded = {
					0, 1, 2, 3, 4, 5, 6, 7, 8,
					9, 17,
					18, 26,
					27, 35,
					36, 44,
					45, 46, 47, 48, 49, 50, 51, 52, 53
			};
			setExcludedSlots(excluded);


			final Button border = new Button(ItemBuilder.of(XMaterial.BLACK_STAINED_GLASS_PANE.toItemStack())
														.name(" ").hideAllFlags(true).build().create());
			for (final int slot : excluded) {//set border before updating default buttons
				setButton(slot, border);
			}

			setBackButtonSlot(45);
			setNextButtonSlot(53);
			setPreviousMenuButtonSlot(0);//back button will null previous one

			setButton(8, new Button(ItemBuilder.of(ReflectUtil.getEnum(Material.class, "POTATO_ITEM", "POTATO"))
											   .name("&5yeet").build().create(),
									(event, clicker, clicked) -> {
										clicker.closeInventory();
										clicker.setVelocity(new Vector(0, 10, 0));
										Chat.send(clicker, "&dyeeted");
									}));

			setAllItems(list);
		}
	}

}
