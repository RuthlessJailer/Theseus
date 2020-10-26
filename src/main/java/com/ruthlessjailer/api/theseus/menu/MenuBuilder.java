/*
package com.ruthlessjailer.api.theseus.menu;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;

*/
/**
 * @author Vadim Hagedorn
 *//*

@Builder(builderClassName = "MenuBaseCreator")
public final class MenuBuilder {

	private final MenuBase                 parent;
	@Builder.Default
	private final String                   title = "Menu";
	@Builder.Default
	private final int                      size  = 9 * 3;
	@Builder.Default
	private final InventoryType            type  = InventoryType.CHEST;
	@Singular
	private final Map<Integer, ButtonBase> buttons;

	public static MenuBaseCreator builder(@NonNull final String title) {
		return new MenuBaseCreator().title(title);
	}

	public static MenuBaseCreator builder(@NonNull final String title, final int size) {
		return builder(title).size(size);
	}

	public static MenuBaseCreator builder(@NonNull final String title, final int size, @NonNull final InventoryType type) {
		return builder(title, size).type(type);
	}

	public static MenuBaseCreator of(@NonNull final MenuBase menu) {
		return builder(menu.getTitle(), menu.getSize(), menu.getType()).parent(menu.getParent()).buttons(menu.getButtons());
	}

	public static class MenuBaseCreator {
		protected final Map<Integer, ButtonBase> buttons = new HashMap<>();

		protected MenuBaseCreator() {}

		public MenuBase build() {
			final MenuBase menu = new MenuBase(this.parent, this.size, this.title, this.type) {};

			for (final Map.Entry<Integer, ButtonBase> entry : this.buttons.entrySet()) {
				menu.addButton(entry.getKey(), entry.getValue());
			}

			return menu;
		}

		public MenuBaseCreator addButton(final int slot, @NonNull final ButtonBase button) {
			return this.button(slot, button);
		}

		public MenuBaseCreator addButtons(@NonNull final Map<Integer, ButtonBase> buttons) {
			buttons.forEach(this::button);
			return this;
		}

		public MenuBaseCreator size(final int size) {
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
*/
