package com.ruthlessjailer.api.theseus.menu;

import com.google.common.primitives.Ints;
import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang.ArrayUtils;
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

	protected static final String            DESTINATION_PAGE_PLACEHOLDER = "${DESTINATION}";
	protected static final String            CURRENT_PAGE_PLACEHOLDER     = "${PAGE}";
	protected static final String            TOTAL_PAGES_PLACEHOLDER      = "${MAX}";
	private final          List<MenuPage<I>> pages                        = new ArrayList<>();
	private final          List<I>           allItems                     = new ArrayList<>();
	@Setter(AccessLevel.PROTECTED)
	private                String            noMorePagesMessage           = "&4&lX&c";//this is used in place ${DESTINATION} on the last and first pages
	private                int[]             includedSlots                = new int[]{
			1, 2, 3, 4, 5, 6, 7,
			10, 11, 12, 13, 14, 15, 16,
			19, 20, 21, 22, 23, 24, 25
	};
	private                int               backButtonSlot               = 0;
	private                int               nextButtonSlot               = 8;
	private                Button            backButton;
	private                Button            nextButton;

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

		final ItemBuilder.ItemStackCreator button = ItemBuilder.of(Material.ARROW).hideAllFlags(true);

		setBackButton(button.name("&6&m&l<-&c&a " + DESTINATION_PAGE_PLACEHOLDER + "&8/&9" + TOTAL_PAGES_PLACEHOLDER).build().create());
		setNextButton(button.name("&a" + DESTINATION_PAGE_PLACEHOLDER + "&8/&9" + TOTAL_PAGES_PLACEHOLDER + " &6&m&l->").build().create());
	}

	/**
	 * Get the number of pages that there is supposed to be. Note that this does not reflect what is currently being displayed.
	 *
	 * @return the expected number of pages based on {@link ListMenu#allItems}
	 *
	 * @see ListMenu#regenerateInventory()
	 */
	protected int getPageCount() {
		return Math.max(this.allItems.size() / this.includedSlots.length, 1);
	}

	/**
	 * Sets the next button for each page.
	 *
	 * @param item the {@link ItemStack item} to use as the next button
	 */
	protected void setNextButton(@NonNull final ItemStack item) {
		this.nextButton = new Button(item);
		setButton(this.nextButtonSlot, this.nextButton);
	}

	/**
	 * Sets the next button for each page.
	 *
	 * @param slot the slot to put it
	 *
	 * @throws IllegalArgumentException if the provided slot is not free
	 */
	protected void setNextButtonSlot(final int slot) {
		if (this.backButtonSlot == slot || !isFreeSlot(slot)) {
			throw new IllegalArgumentException("Provided slot " + slot + " is not free!");
		}

		this.nextButtonSlot = slot;

		setButton(slot, this.nextButton);
	}

	/**
	 * Sets the back button for each page.
	 *
	 * @param item the {@link ItemStack item} to use as the back button
	 */
	protected void setBackButton(@NonNull final ItemStack item) {
		this.backButton = new Button(item);
		setButton(this.backButtonSlot, this.backButton);
	}

	/**
	 * Sets the back button for each page.
	 *
	 * @param slot the slot to put it
	 *
	 * @throws IllegalArgumentException if the provided slot is not free
	 */
	protected void setBackButtonSlot(final int slot) {
		if (this.nextButtonSlot == slot || !isFreeSlot(slot)) {
			throw new IllegalArgumentException("Provided slot " + slot + " is not free!");
		}

		this.backButtonSlot = slot;

		setButton(slot, this.backButton);
	}

	private Map<Integer, Button> cloneButtons(final Map<Integer, Button> toClone) {
		final Map<Integer, Button> clone = new HashMap<>();

		(toClone == null ? this.buttons : toClone).forEach((slot, button) -> clone.put(slot, new Button(button.getItem().clone(), button.getAction())));

		return clone;
	}

	private void formatNames(@NonNull final Map<Integer, Button> buttons) {
		buttons.forEach((slot, button) -> {
			final ItemBuilder.ItemStackCreator builder = ItemBuilder.of(button.getItem());

//			try {
//				builder.name(formatName(button.getItem(), parseRange(Integer.parseInt(button.getItem().getItemMeta().getLocalizedName()))));
//			} catch (final NumberFormatException ignored) {}//localized name can be null; doesn't have to have page number

			button.setItem(builder.name(formatName(button.getItem(), this.pages.size())).build().create());//this is called during page generation
		});
	}

	private String formatName(@NonNull final ItemStack item, final int destinationPage) {
		return item.getItemMeta().getDisplayName()
				   .replace(CURRENT_PAGE_PLACEHOLDER, String.valueOf(this.pages.size()))//this is called during page generation
				   .replace(DESTINATION_PAGE_PLACEHOLDER, String.valueOf(destinationPage))
				   .replace(TOTAL_PAGES_PLACEHOLDER, String.valueOf(getPageCount()));
	}

	private String formatName(@NonNull final ItemStack item, @NonNull final String destinationPage) {
		return item.getItemMeta().getDisplayName()
				   .replace(CURRENT_PAGE_PLACEHOLDER, String.valueOf(this.pages.size()))//this is called during page generation
				   .replace(DESTINATION_PAGE_PLACEHOLDER, destinationPage + "&c")
				   .replace(TOTAL_PAGES_PLACEHOLDER, String.valueOf(getPageCount()));
	}

	private String formatTitle() {
		return getTitle()
				.replace(CURRENT_PAGE_PLACEHOLDER, String.valueOf(this.pages.size() + 1))//this is called before the page is added to the list
				.replace(TOTAL_PAGES_PLACEHOLDER, String.valueOf(getPageCount()));
	}

	private int parseRange(final int page) {
		return Ints.constrainToRange(page, 0, getPageCount() > 1 ? getPageCount() - 1 : 0);
	}

	/**
	 * Set the slots that will be filled with the list items.
	 *
	 * @param slots the slots to use
	 *
	 * @see ListMenu#setExcludedSlots(int...) for the inverse of this method
	 */
	protected final void setIncludedSlots(final int... slots) {
		final List<Integer> included = Arrays.stream(slots).filter(i -> i < MAX_SLOTS).boxed().collect(Collectors.toList());//convert to list for ease of parsing

		cloneButtons(null).forEach((slot, button) -> {
			if (included.contains(slot)) {
				this.buttons.remove(slot);
			}
		});

		this.includedSlots = ArrayUtils.toPrimitive(included.toArray(new Integer[0]));
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

		cloneButtons(null).forEach((slot, button) -> {
			if (included.contains(slot)) {
				this.buttons.remove(slot);
			}
		});

		System.arraycopy(ArrayUtils.toPrimitive(included.toArray(new Integer[0])), 0, this.includedSlots, 0, this.includedSlots.length);//fill array
	}

	/**
	 * Sets a button if it is not in an excluded slot.
	 *
	 * @param slot   the slot to put the button
	 * @param button the {@link Button} to set
	 */
	@Override
	protected void setButton(final int slot, final Button button) {
		if (isFreeSlot(slot)) {
			super.setButton(slot, button);
		} else if (button == null) {
			super.setButton(slot, null);
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
			setButtonsInMenu(new MenuPage<>(getSize(), formatTitle(), this.includedSlots), 0, cloneButtons(null));
			this.pages.forEach(MenuPage::regenerateInventory);
			return;
		}

		final List<I> buf = new ArrayList<>();
		int           i   = 0;//counter
		int           j   = this.allItems.size();//reverse
		int           p   = 0;//page counter
		for (final I item : this.allItems) {//parse the pages
			buf.add(item);
			if (i == this.includedSlots.length || j <= this.includedSlots.length) {
				setButtonsInMenu(new MenuPage<>(getSize(), formatTitle(), this.includedSlots, buf), p, cloneButtons(null));
				p++;
				i = 0;
				buf.clear();
			}
			i++;
			j--;
		}

		this.pages.forEach(MenuPage::regenerateInventory);
	}

	/**
	 * Fills the pages with items and buttons.
	 */
	@Override
	protected void refillInventory() {
		if (this.allItems.isEmpty()) {
			setButtonsInMenu(this.pages.get(0), 1, cloneButtons(null));
			this.pages.forEach(MenuPage::refillInventory);
			return;
		}

		final List<I> buf = new ArrayList<>();
		int           i   = 0;//counter
		int           p   = 0;//page counter
		for (final I item : this.allItems) {//parse the pages
			buf.add(item);
			if (i == this.includedSlots.length - 1 || (this.allItems.size() < this.includedSlots.length && i == this.allItems.size() - 1)) {
				this.pages.get(p).setItems(buf);
				setButtonsInMenu(this.pages.get(p), p, cloneButtons(null));
				i = 0;
				p++;
				buf.clear();
			}
			i++;
		}

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
			this.pages.get(parseRange(Integer.parseInt(clicked.getItem().getItemMeta().getLocalizedName()))).displayTo(clicker);
		};

		cloneButtons(buttons).forEach((slot, button) -> setButtonInMenu(pageNumber, slot, button, buttons, button.getAction()));

		//these override the above loop for the paging buttons
		setButtonInMenu(pageNumber + 1, this.nextButtonSlot, this.nextButton, buttons, action);
		setButtonInMenu(pageNumber - 1, this.backButtonSlot, this.backButton, buttons, action);

		formatNames(buttons);

		buttons.forEach(page::setButton);
		this.pages.add(page);
	}

	private void setButtonInMenu(final int destinationPageNumber, final int slot, @NonNull final Button button, @NonNull final Map<Integer, Button> buttons,
								 @NonNull final ButtonAction action) {

		final String name = destinationPageNumber != parseRange(destinationPageNumber) &&
							(parseRange(destinationPageNumber) + 1 == getPageCount() || parseRange(destinationPageNumber) == 0)
							? formatName(button.getItem(), this.noMorePagesMessage)
							: formatName(button.getItem(), parseRange(destinationPageNumber) + 1);

		final Button modified = new Button(ItemBuilder.of(button.getItem())
													  .name(name)
													  .localizedName(String.valueOf(destinationPageNumber)).build().create(),
										   action);

		buttons.put(slot, modified);
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
		if (this.includedSlots == null) {
			return true;
		}

		if (slot > MAX_SLOTS || slot < 0) {
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
