package com.ruthlessjailer.api.theseus.menu;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Only ONE player per instance.
 * If {@link MenuBase#displayTo(Player)} is called twice, an {@link UnsupportedOperationException} will be thrown.
 *
 * @author Vadim Hagedorn
 */
@Getter
public abstract class MenuBase implements Listener {

	private final Map<Integer, ButtonBase> buttons = new HashMap<>();

	private final MenuBase      parent;
	private final String        title;
	private final int           size;
	private final InventoryType type;
	private       MenuView      view;
	private       MenuHolder    holder;

	public MenuBase() {
		this(9 * 3, "Menu");
	}

	public MenuBase(final int size, @NonNull final String title) {
		this(size, title, InventoryType.CHEST);
	}

	public MenuBase(final int size, @NonNull final String title, @NonNull final InventoryType type) {
		this(null, size, title, InventoryType.CHEST);
	}

	public MenuBase(final MenuBase parent, final int size, @NonNull final String title) {
		this(null, size, title, InventoryType.CHEST);
	}

	public MenuBase(final MenuBase parent, final int size, @NonNull final String title, @NonNull final InventoryType type) {
		this.parent = parent;
		this.size   = size;
		this.title  = Chat.colorize(title);
		this.type   = type;

		Bukkit.getPluginManager().registerEvents(this, Checks.instanceCheck(String.format(
				"Plugin instance cannot be null when initializing menu listener %s.",
				ReflectUtil.getPath(this.getClass()))));
	}

	protected final void addButton(final int slot, @NonNull final ButtonBase button) {
		this.buttons.put(slot, button);
	}


	public final void displayTo(@NonNull final Player player) {


		if (this.view != null || this.holder != null) {

		}


		final Inventory inventory = Bukkit.createInventory(new MenuHolder(player.getUniqueId()), this.size);

		for (final Map.Entry<Integer, ButtonBase> entry : this.buttons.entrySet()) {
			final Integer    slot   = entry.getKey();
			final ButtonBase button = entry.getValue();

			inventory.setItem(slot, button.getItem());
		}

		this.view   = new MenuView(inventory, player.getInventory(), this.title, player);
		this.holder = new MenuHolder(player.getUniqueId());
	}

	@Override
	public MenuBase clone() {
		return MenuBuilder.of(this).build();
	}

	@EventHandler
	public void onClick(final InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		if (!event.getView().equals(this.view)) {
			return;
		}

		if (!MenuHolder.equals(this.holder, event.getInventory().getHolder())) {
			return;
		}

		//now we know it's the right menu

		for (final Map.Entry<Integer, ButtonBase> entry : this.buttons.entrySet()) {
			final Integer    slot   = entry.getKey();
			final ButtonBase button = entry.getValue();

			if (button.getItem().isSimilar(event.getCurrentItem())) {
				switch (button.getType()) {
					case INFO:
						event.setCancelled(true);
						break;
					case TAKE:
						event.setCancelled(false);
						break;
					case ACTION:
						Checks.nullCheck(button.getAction(), "Button action cannot be null as its type is ACTION!");
						button.getAction().onClick((Player) event.getWhoClicked(), event.getClick(), event.getCurrentItem());
				}
			}
		}
	}

}
