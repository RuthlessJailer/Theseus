package com.ruthlessjailer.api.theseus.delete.example;

import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import com.ruthlessjailer.api.theseus.menu.ListItem;
import com.ruthlessjailer.api.theseus.menu.ListMenu;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import com.ruthlessjailer.api.theseus.menu.button.ActionButton;
import com.ruthlessjailer.api.theseus.menu.button.ButtonBase;
import com.ruthlessjailer.api.theseus.menu.button.DynamicButton;
import com.ruthlessjailer.api.theseus.menu.button.MenuButton;
import com.ruthlessjailer.api.theseus.multiversion.XColor;
import com.ruthlessjailer.api.theseus.multiversion.XMaterial;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author RuthlessJailer
 */
public class TestMenu extends MenuBase {

	public TestMenu() {
		super(MIN_SLOTS, "&3Test Menu");

		setButton(0, new ActionButton(ItemBuilder.of(Material.BEDROCK, "&eCUSTOMIZABLE ITEM").build().create(), (event, clicker, clicked) -> {

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

		setButton(8, new MenuButton(ItemBuilder.of(Material.SAND, "&2List Menu").build().create(), new YeetMenu()));

		final CopyOnWriteArrayList<ButtonBase> buttons = new CopyOnWriteArrayList<>();

		for (final XColor x : XColor.values()) {
			final ItemStack item = ItemBuilder.of(Material.STONE, x + x.name()).build().create();
			x.applyTo(item, "WHITE_CONCRETE");
			buttons.add(new ActionButton(item));
		}

		setButton(3, new DynamicButton(this, 5, true, buttons));
		System.out.println("DYNAMIC BUTTON");

		final ItemStack gold    = ItemBuilder.of(Material.GOLD_BLOCK, "&6GOLD").build().create();
		final ItemStack diamond = ItemBuilder.of(Material.DIAMOND_BLOCK, "&9DIAMOND").build().create();

		setButton(4, new ActionButton(gold, (event, clicker, clicked) -> {

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
			super(TestMenu.this, MAX_SLOTS, "&3List &a" + CURRENT_PAGE_PLACEHOLDER + "&8/&9" + TOTAL_PAGES_PLACEHOLDER);

			final List<ListItem> list = new ArrayList<>();
//			final List<String>     items = Arrays.asList("f7c77d99-9f15-4a66-a87d-c4a51ef30d19",//hypickle
//														 "8f2340da-e9c6-46a6-b1f2-e8976f1bbfa2",//me
//														 "8f2340da-e9c6-46a6-b1f2-e8976f1bbfa2",//nate
//														 "71b55338-d9cb-42f8-91a2-e7bddfabd7f6",//ae
//														 "9d437d43-a14f-4659-bb77-005daf649628",//trq
//														"069a79f4-44e9-4726-a5be-fca90e38aaf5",//notch
//														 "69e8f7d5-11f9-4818-a3bb-7f237df32949",//xfuzzy
//														 "ec70bcaf-702f-4bb8-b48d-276fa52a780c",//dream
//														"bd3dd5a4-0438-4699-b2fd-36f518154b41",//george
//														 "c66f7c8a-ed0c-4469-90b0-421d8ff7ca49",//sapnap
//														 "dec1e392-93ba-43fd-9c3f-7be6d6715fcf",//someone idk
//														 "e8889e49-732e-4d2e-bc61-26ebda3d78ea",//kx
//														 "dba273aa-d92c-488b-bbdc-ec583c075e4c",//chunga
//														"8e1f9dfb-8881-495f-856b-e19d31c036e1"//bbl
//														);
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


			final ActionButton border = new ActionButton(ItemBuilder.of(XMaterial.BLACK_STAINED_GLASS_PANE.toItemStack())
																	.name(" ").hideAllFlags(true).build().create());
			for (final int slot : excluded) {//set border before updating default buttons
				setButton(slot, border);
			}

			setBackButtonSlot(45);
			setNextButtonSlot(53);
			setPreviousMenuButtonSlot(0);

			setButton(8, new ActionButton(ItemBuilder.of(XMaterial.BARRIER.toItemStack())
													 .name("&4Close").build().create(),
										  (event, clicker, clicked) -> {
											  clicker.closeInventory();
										  }));

			setAllItems(list);
		}
	}

}
