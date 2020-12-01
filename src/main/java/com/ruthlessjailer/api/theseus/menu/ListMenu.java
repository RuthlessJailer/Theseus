package com.ruthlessjailer.api.theseus.menu;

import com.google.common.primitives.Ints;
import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author RuthlessJailer
 */
@Getter
public abstract class ListMenu<I extends ListItem> extends MenuBase {

	private final List<MenuPage<I>> pages         = new ArrayList<>();
	private final List<I>           allItems      = new ArrayList<>();
	private       int[]             includedSlots = new int[]{
			1, 2, 3, 4, 5, 6, 7,
			10, 11, 12, 13, 14, 15, 16,
			19, 20, 21, 22, 23, 24, 25
	};

	private Pair<Integer, Button> backButton;
	private Pair<Integer, Button> nextButton;

	private ListMenu(final @NonNull InventoryType type, final @NonNull String title) {//can't use type for paged menu
		this(null, type, title);
	}

	private ListMenu(final MenuBase parent, final @NonNull InventoryType type, final @NonNull String title) {//can't use type for paged menu
		super(parent, type, title);
	}

	public ListMenu(final int size, final @NonNull String title) {
		this(null, size, title);
	}

	public ListMenu(final MenuBase parent, final int size, final @NonNull String title) {
		super(parent, size, title);

		final ItemBuilder.ItemStackCreator back = ItemBuilder.of(Material.ARROW).hideAllFlags(true).name("&6&m&l<-&c&9 ${PAGE}&8/&9${MAX}");

		setBackButton(0, back.build().create());
		setNextButton(8, back.name("&9${DESTINATION}&8/&9${MAX} &6&m&l->").build().create());
	}

	/**
	 * Sets the next button for each page.
	 *
	 * @param slot the slot to put it
	 * @param item the {@link ItemStack item} to use as the next button
	 *
	 * @throws IllegalArgumentException if the provided slot is not free
	 */
	protected void setNextButton(final int slot, @NonNull final ItemStack item) {
		if ((this.backButton != null && this.backButton.getKey() == slot) || !isFreeSlot(slot)) {
			throw new IllegalArgumentException("Provided slot " + slot + " is not free!");
		}

		this.nextButton = new Pair<>(slot, new Button(item, ButtonAction.EMPTY_ACTION));//logic is done
		// when the pages
		// are created

		setButton(slot, this.nextButton.getValue());
	}

	/**
	 * Sets the back button for each page.
	 *
	 * @param slot the slot to put it
	 * @param item the {@link ItemStack item} to use as the back button
	 *
	 * @throws IllegalArgumentException if the provided slot is not free
	 */
	protected void setBackButton(final int slot, @NonNull final ItemStack item) {
		if ((this.nextButton != null && this.nextButton.getKey() == slot) || !isFreeSlot(slot)) {
			throw new IllegalArgumentException("Provided slot " + slot + " is not free!");
		}

		this.backButton = new Pair<>(slot, new Button(item, ButtonAction.EMPTY_ACTION));//logic is done when pages are created

		setButton(slot, this.backButton.getValue());
	}

	private Map<Integer, Button> cloneButtons() {
		final Map<Integer, Button> clone = new HashMap<>();

		this.buttons.forEach((slot, button) -> clone.put(slot, new Button(button.getItem(), button.getAction())));

		return clone;
	}

	private void formatNames(@NonNull final Map<Integer, Button> buttons) {
		buttons.forEach((slot, button) -> {
			button.setItem(ItemBuilder.of(button.getItem())
									  .name(formatName(button.getItem(), Integer.parseInt(button.getItem().getItemMeta().getLocalizedName())))
									  .build().create());
		});
	}

	private String formatName(@NonNull final ItemStack item, final int destinationPage) {
		return item.getItemMeta().getDisplayName()
				   .replace("${PAGE}", String.valueOf(this.pages.size()))
				   .replace("${DESTINATION}", String.valueOf(destinationPage))
				   .replace("${MAX}", String.valueOf(this.allItems.size() / this.includedSlots.length));
	}

	/**
	 * Set the slots that will be filled with the list items.
	 *
	 * @param slots the slots to use
	 *
	 * @see ListMenu#setExcludedSlots(int...) for the inverse of this method
	 */
	protected final void setIncludedSlots(final int... slots) {
		this.includedSlots = slots;
	}

	/**
	 * Adds all {@link ListItem items} in a {@link List} to the list. Note that this will not update the pages; to update call
	 * {@link ListMenu#regenerateInventory()}.
	 *
	 * @param items the {@link List} of {@link ListItem items} to add
	 */
	protected final void addItems(@NonNull final List<I> items) {
		this.allItems.addAll(items);
	}

	/**
	 * Adds an {@link ListItem item} to the list. Note that this will not update the pages; to update call {@link ListMenu#regenerateInventory()}.
	 *
	 * @param item the {@link ListItem item} to add
	 */
	protected final void addItem(@NonNull final I item) {
		this.allItems.add(item);
	}


	/**
	 * Set the items to be displayed. Note that this will not update the pages; to update call {@link ListMenu#regenerateInventory()}.
	 *
	 * @param items the new {@link List} of {@link ListItem items} to be displayed
	 */
	protected final void setAllItems(@NonNull final List<I> items) {
		this.allItems.clear();
		this.allItems.addAll(items);
	}

	/**
	 * Set the slots that will not be filled with the list items.
	 *
	 * @param slots the slots to not use
	 *
	 * @see ListMenu#setIncludedSlots(int...) for the inverse of this method
	 */
	protected final void setExcludedSlots(final int... slots) {
		if (slots.length >= MAX_SLOTS) {
			throw new IllegalArgumentException("Max number of slots is " + MAX_SLOTS);
		}


		final List<Integer> excluded = Arrays.stream(slots).filter(i -> i < MAX_SLOTS).boxed().collect(Collectors.toList());//convert to list for ease of parsing
		final List<Integer> included = new ArrayList<>(MAX_SLOTS - excluded.size());//make a list for ease of parsing

		this.includedSlots = new int[MAX_SLOTS - excluded.size()];//reset array

		for (int i = 0; i < MAX_SLOTS; i++) {//fill list
			if (!excluded.contains(i)) {
				included.add(i);
			}
		}

		System.arraycopy(this.includedSlots, 0, ArrayUtils.toPrimitive(included.toArray(new Integer[0])), 0, this.includedSlots.length);//fill array
	}

	/**
	 * Sets a button if it is not in an excluded slot.
	 *
	 * @param slot   the slot to put the button
	 * @param button the {@link Button} to set
	 */
	@Override
	protected void setButton(final int slot, final @NonNull Button button) {
		if (isFreeSlot(slot)) {
			super.setButton(slot, button);
		}
	}

	/**
	 * Refills the inventories and updates all viewers with changes.
	 */
	@Override
	protected void updateInventory() {
		generateInventory();

		refillInventory();

		this.pages.forEach(MenuPage::updateInventory);
	}

	/**
	 * Creates pages if there aren't any already.
	 */
	@Override
	protected void generateInventory() {
		if (!this.pages.isEmpty()) {
			return;
		}

		regenerateInventory();
	}

	/**
	 * Creates menu pages.
	 */
	@Override
	protected void regenerateInventory() {

		this.pages.clear();

		if (this.allItems.isEmpty()) {//create an empty page
			setButtonsInMenu(new MenuPage<>(getSize(), "Page 1", this.includedSlots), 1, cloneButtons());
			return;
		}

		final List<I> buf = new ArrayList<>();
		int           i   = 0;//counter
		int           p   = 0;//page counter
		for (final I item : this.allItems) {//parse the pages
			buf.add(item);
			if (i == this.includedSlots.length) {
				setButtonsInMenu(new MenuPage<>(getSize(), "Page " + (p + 1), this.includedSlots, buf), p, cloneButtons());
				i = 0;
				p++;
				buf.clear();
			}
			i++;
		}

		this.pages.forEach(MenuPage::regenerateInventory);
	}

	/**
	 * Fills the pages with items and buttons.
	 */
	@Override
	protected void refillInventory() {
		this.pages.forEach(MenuPage::refillInventory);
	}

	/**
	 * Displays the menu to a player.
	 *
	 * @param player the player to display the menu to.
	 */
	@Override
	public void displayTo(final @NonNull Player player) {
		generateInventory();

		this.pages.get(0).displayTo(player);
	}

	private void setButtonsInMenu(@NonNull final MenuPage<I> page, final int pageNumber, @NonNull final Map<Integer, Button> buttons) {
		final ButtonAction action = (event, clicker, clicked) -> {
			final int next = Integer.parseInt(clicked.getItem().getItemMeta().getLocalizedName());
			this.pages.get(Ints.constrainToRange(next, 0, (this.allItems.size() / this.includedSlots.length) - 1)).displayTo(clicker);
		};

		setButtonInMenu(pageNumber + 1, this.nextButton, buttons, action);
		setButtonInMenu(pageNumber - 1, this.backButton, buttons, action);

		formatNames(buttons);

		buttons.forEach(page::setButton);
		this.pages.add(page);
	}

	private void setButtonInMenu(final int destinationPageNumber, @NonNull final Pair<Integer, Button> button, @NonNull final Map<Integer, Button> buttons, @NonNull final ButtonAction action) {

		final Button modified = new Button(ItemBuilder.of(button.getValue().getItem())
													  .name(formatName(button.getValue().getItem(), destinationPageNumber))
													  .localizedName(String.valueOf(destinationPageNumber)).build().create(),
										   action);

		buttons.put(button.getKey(), modified);
	}

	/**
	 * Checks if the given slot is not contained by {@code includedSlots}.
	 *
	 * @param slot the slot to check
	 *
	 * @return {@code true} if not, {@code false} if it is
	 *
	 * @see ListMenu#setIncludedSlots(int...)
	 * @see ListMenu#setExcludedSlots(int...)
	 */
	protected boolean isFreeSlot(final int slot) {
		if (slot >= MAX_SLOTS) {
			return false;
		}

		for (final int includedSlot : this.includedSlots) {
			if (includedSlot == slot) {
				return false;
			}
		}

		return true;
	}
}
