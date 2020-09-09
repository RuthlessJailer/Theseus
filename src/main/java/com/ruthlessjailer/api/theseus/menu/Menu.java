package com.ruthlessjailer.api.theseus.menu;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vadim Hagedorn
 */
@Builder
public final class Menu {

	static {

	}

	private final MenuBase                 parent;
	@Builder.Default
	private final String                   title = "Menu";
	@Builder.Default
	private final int                      size  = 9 * 3;
	@Builder.Default
	private final InventoryType            type  = InventoryType.CHEST;
	@Singular
	private final Map<Integer, ButtonBase> buttons;

	public static MenuBuilder builder(@NonNull final String title) {
		return new MenuBuilder().title(title);
	}

	public static MenuBuilder builder(@NonNull final String title, final int size) {
		return builder(title).size(size);
	}

	public static MenuBuilder builder(@NonNull final String title, final int size, @NonNull final InventoryType type) {
		return builder(title, size).type(type);
	}

	public static MenuBuilder of(@NonNull final MenuBase menu) {
		return builder(menu.getTitle(), menu.getSize(), menu.getType()).parent(menu.getParent()).buttons(menu.getButtons());
	}

	public static class MenuBuilder {
		protected final Map<Integer, ButtonBase> buttons = new HashMap<>();

		public MenuBase build() {
			final MenuBase menu = new MenuBase(this.parent, this.size, this.title, this.type) {};

			for (final Map.Entry<Integer, ButtonBase> entry : this.buttons.entrySet()) {
				menu.addButton(entry.getKey(), entry.getValue());
			}

			return menu;
		}


		public MenuBuilder size(final int size) {
			if (size % 9 != 0) {
				throw new IllegalArgumentException("Size must be a multiple of 9!");
			}

			if (size / 9 < 1 || size / 9 > 6) {
				throw new IllegalArgumentException("Size invalid!");
			}

			this.size = size;
			return this;
		}
	}
}
