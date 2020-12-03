package com.ruthlessjailer.api.theseus.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RuthlessJailer
 */
@Getter
public final class MenuPage<I extends ListItem> extends MenuBase {

	private final List<I> items = new ArrayList<>();
	@Setter(AccessLevel.PROTECTED)
	private       int[]   includedSlots;

	public MenuPage(final int size, @NonNull final String title, final int[] includedSlots) {
		this(size, title, includedSlots, new ArrayList<>());
	}

	public MenuPage(final int size, @NonNull final String title, final int[] includedSlots, @NonNull final List<I> items) {
		super(size, title);
		this.includedSlots = includedSlots;
		setItems(items);
	}

	public void setItems(@NonNull final List<I> items) {
		this.items.clear();
		this.items.addAll(items);

		int i = 0;

		for (final int slot : this.includedSlots) {
			if (i == items.size()) {
				break;
			}

			setButton(slot, new Button(this.items.get(i).item(), this.items.get(i).action()));
//			System.out.println(slot + ":" + this.items.get(i).item().getData());
			i++;
		}
	}

	/**
	 * Fills the inventory with the list items and buttons.
	 */
	@Override
	protected void refillInventory() {
		super.refillInventory();
		setItems(new ArrayList<>(this.items));
	}
}