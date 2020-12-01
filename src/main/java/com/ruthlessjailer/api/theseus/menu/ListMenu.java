package com.ruthlessjailer.api.theseus.menu;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author RuthlessJailer
 */
@Getter
public abstract class ListMenu<I extends ListItem> extends MenuBase {

	private final List<MenuPage> pages    = new ArrayList<>();
	private final List<I>        allItems = new ArrayList<>();
	private       int[]          includedSlots;

	public ListMenu(final @NonNull InventoryType type, final @NonNull String title) {
		super(type, title);
	}

	public ListMenu(final int size, final @NonNull String title) {
		super(size, title);
	}

	public ListMenu(final MenuBase parent, final int size, final @NonNull String title) {
		super(parent, size, title);
	}

	public ListMenu(final MenuBase parent, final @NonNull InventoryType type, final @NonNull String title) {
		super(parent, type, title);
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
		for (final int includedSlot : this.includedSlots) {
			if (slot == includedSlot) {
				return;
			}
		}
		super.setButton(slot, button);
	}

	/**
	 * Refills the inventories and updates all viewers with changes.
	 */
	@Override
	protected void updateInventory() {
		this.pages.forEach(MenuBase::updateInventory);
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
		final List<I> clone = new ArrayList<>(this.allItems);
		int           i     = 0;
		for (final I item : this.allItems) {
			clone.remove(item);
			if (i == this.includedSlots.length) {
				this.pages.add(new MenuPage<I>(getSize()));
				i = 0;
			}
			i++;
		}

		this.pages.forEach(MenuBase::regenerateInventory);
	}

	/**
	 * Fills the pages with items and buttons.
	 */
	@Override
	protected void refillInventory() {
		this.pages.forEach(MenuBase::refillInventory);
	}

	/**
	 * Displays the menu to a player.
	 *
	 * @param player the player to display the menu to.
	 */
	@Override
	public void displayTo(final @NonNull Player player) {
		this.pages.get(0).displayTo(player);
	}
}
