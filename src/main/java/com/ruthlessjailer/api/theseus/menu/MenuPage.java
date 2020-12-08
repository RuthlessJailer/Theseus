package com.ruthlessjailer.api.theseus.menu;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
	@SneakyThrows
	protected CompletableFuture<MenuBase> refillInventory() {
		return CompletableFuture.supplyAsync(() -> {
			setItems(new ArrayList<>(this.items));
			try {
				return super.refillInventory().get();
			} catch (final InterruptedException | ExecutionException e) {
				throw new RuntimeException(MenuBase.MENU_ERROR_MESSAGE, e);
			}
		});
	}
}
