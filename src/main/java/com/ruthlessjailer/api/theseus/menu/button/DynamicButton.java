package com.ruthlessjailer.api.theseus.menu.button;

import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import com.ruthlessjailer.api.theseus.task.delete.manager.TaskManager;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author RuthlessJailer
 */
@Getter
@Setter
public class DynamicButton extends ButtonBase implements Runnable {

	private static final String BUTTON_ERROR_MESSAGE = "There must be at least 1 button at all times.";

	private final Object           runLock = new Object();
	private final List<ButtonBase> buttons;
	private       MenuBase         menu;
	private       int              index;
	private       boolean          random;
	private       int              interval;

	public DynamicButton(final MenuBase menu, final int interval) {
		this(menu, interval, false);
	}

	public DynamicButton(final MenuBase menu, final int interval, final boolean random) {
		this(menu, interval, random, new CopyOnWriteArrayList<>());
	}

	public DynamicButton(final MenuBase menu, final int interval, final boolean random, final CopyOnWriteArrayList<ButtonBase> buttons) {
		super(buttons.size() >= 1 ? buttons.get(0).getItem() : new ItemStack(Material.STONE));
		Checks.verify(buttons.size() > 1, BUTTON_ERROR_MESSAGE, IllegalArgumentException.class);

		this.menu     = menu;
		this.interval = interval;
		this.random   = random;
		this.buttons  = buttons;
		this.index    = 0;

		TaskManager.async.repeat(this, interval);
	}

	public void addButton(@NonNull final ButtonBase button) {
		this.buttons.add(button);
	}

	public void removeButton(@NonNull final ButtonBase button) {
		Checks.verify(this.buttons.size() > 1, BUTTON_ERROR_MESSAGE, UnsupportedOperationException.class);

		this.buttons.remove(button);
	}

	public void setButtons(@NonNull final List<ButtonBase> buttons) {
		Checks.verify(buttons.size() >= 1, BUTTON_ERROR_MESSAGE, IllegalArgumentException.class);

		this.buttons.clear();
		this.buttons.addAll(buttons);
	}

	@Override
	public void onClick(final @NonNull InventoryClickEvent event, final @NonNull Player clicker, final @NonNull ButtonBase clicked) {
		this.buttons.get(this.index).onClick(event, clicker, clicked);
	}

	@Override
	public ItemStack getItem() {
		return this.buttons.get(this.index).getItem();
	}

	@Override
	public void setItem(final ItemStack item) {
		this.buttons.get(this.index).setItem(item);
	}

	@Override
	public void run() {
		synchronized (this.runLock) {
			if (this.random) {
				this.index = Common.RANDOM.nextInt(this.buttons.size());
			} else {
				if (this.index >= this.buttons.size()) {
					this.index = 0;
				} else {
					this.index++;
				}
			}

			this.menu.updateInventory();
		}
	}
}
