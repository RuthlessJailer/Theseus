package com.ruthlessjailer.api.theseus.menu;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author RuthlessJailer
 */
public final class MenuPage<I extends ListItem> extends MenuBase {

	@Getter
	private final List<I> items = new ArrayList<>();

	public MenuPage(final int size) {
		this(size, Collections.emptyList());//TODO
	}

	public MenuPage(final int size, @NonNull final List<I> items) {
		super(size, "TODO");//TODO
		this.items.addAll(items);
	}

	public void setItems(@NonNull final List<I> items) {
		this.items.clear();
		this.items.addAll(items);
	}
}
