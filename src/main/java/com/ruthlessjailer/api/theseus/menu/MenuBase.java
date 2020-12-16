package com.ruthlessjailer.api.theseus.menu;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import com.ruthlessjailer.api.theseus.menu.button.ActionButton;
import com.ruthlessjailer.api.theseus.menu.button.ButtonAction;
import com.ruthlessjailer.api.theseus.menu.button.ButtonBase;
import com.ruthlessjailer.api.theseus.task.handler.FutureHandler;
import com.ruthlessjailer.api.theseus.task.manager.TaskManager;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @author RuthlessJailer
 */
@Getter
public abstract class MenuBase {

	public static final String                   MENU_ERROR_MESSAGE       = "There was a problem with the menu. :(";
	public static final String                   NBT_CURRENT_MENU         = "THESEUS_CURRENT_MENU";
	public static final String                   NBT_PREVIOUS_MENU        = "THESEUS_PREVIOUS_MENU";
	public static final int                      MAX_SLOTS                = 54;
	public static final int                      MIN_SLOTS                = 9;
	protected final     Map<Integer, ButtonBase> buttons                  = new ConcurrentHashMap<>();
	private final       MenuBase                 parent;
	private final       String                   title;
	private final       int                      size;
	private final       InventoryType            type;
	private             int                      previousMenuButtonSlot   = 0;
	private             ActionButton             previousMenuButton;
	@Setter(AccessLevel.PROTECTED)
	private             boolean                  enablePreviousMenuButton = true;
	@Setter(AccessLevel.PROTECTED)
	private             boolean                  protectEmptySlots        = true;
	private             Inventory                inventory;
	@Getter(AccessLevel.PRIVATE)
	private final       Object                   inventoryLock            = new Object();

	public MenuBase(@NonNull final InventoryType type, @NonNull final String title) {
		this(null, type, title);
	}

	public MenuBase(final int size, @NonNull final String title) {
		this(null, size, title);
	}

	public MenuBase(final MenuBase parent, final int size, @NonNull final String title) {
		Checks.verify(size <= MAX_SLOTS && size >= MIN_SLOTS, "Size must be between " + MIN_SLOTS + " and " + MAX_SLOTS);

		this.parent = parent;
		this.size   = size;
		this.title  = Chat.colorize(title);
		this.type   = null;

		setPreviousMenuButton(ItemBuilder.of(Material.ARROW, "&9&l&m<-&c&3 Return").hideAllFlags(true).build().create());
	}

	public MenuBase(final MenuBase parent, @NonNull final InventoryType type, @NonNull final String title) {
		this.parent = parent;
		this.size   = 0;
		this.title  = Chat.colorize(title);
		this.type   = type;

		setPreviousMenuButton(ItemBuilder.of(Material.ARROW, "&9&l&m<-&c&3 Return").hideAllFlags(true).build().create());
	}


	/**
	 * Returns the current open menu of the player.
	 *
	 * @param player the {@link Player} to check
	 *
	 * @return the current {@link MenuBase} instance or {@code null}
	 */
	public static MenuBase getCurrentMenu(@NonNull final Player player) {
		return getMenu(player, NBT_CURRENT_MENU);
	}

	/**
	 * Returns the previous menu of the player.
	 *
	 * @param player the {@link Player} to check
	 *
	 * @return the previous {@link MenuBase} instance or {@code null}
	 */
	public static MenuBase getPreviousMenu(@NonNull final Player player) {
		return getMenu(player, NBT_PREVIOUS_MENU);
	}

	/**
	 * Clears all menu-related metadata from the given player.
	 *
	 * @param player the {@link Player} instance to modify
	 */
	public static void clearMetadata(@NonNull final Player player) {
		player.removeMetadata(NBT_PREVIOUS_MENU, Checks.instanceCheck());
		player.removeMetadata(NBT_CURRENT_MENU, Checks.instanceCheck());
	}

	private static MenuBase getMenu(@NonNull final Player player, @NonNull final String tag) {
		if (!player.hasMetadata(tag)) {
			return null;
		}

		final List<MetadataValue> meta = player.getMetadata(tag);

		if (meta.isEmpty()) {
			Chat.warning("Player " + player.getName() + "'s metadata " + tag + " is corrupted; cannot retrieve the menu.");
			return null;
		}

		final MenuBase menu = (MenuBase) meta.get(0).value();

		Checks.nullCheck(menu, "Player " + player.getName() + " is missing metadata tag value " + tag + "; cannot retrieve the menu.");

		return menu;
	}

	/**
	 * Sets the previous menu button.
	 *
	 * @param item the {@link ItemStack item} to use as the previous button
	 */
	protected void setPreviousMenuButton(@NonNull final ItemStack item) {
		this.previousMenuButton = new ActionButton(item, (this.parent == null ? ButtonAction.EMPTY_ACTION : (event, clicker, clicked) -> this.parent.displayTo(clicker)));
		setButton(this.previousMenuButtonSlot, this.previousMenuButton);
	}

	/**
	 * Sets the previous menu button.
	 *
	 * @param slot the slot to put it
	 */
	protected void setPreviousMenuButtonSlot(final int slot) {
		this.previousMenuButtonSlot = slot;
		setButton(this.previousMenuButtonSlot, this.previousMenuButton);
	}

	/**
	 * Sets a button.
	 *
	 * @param slot   the slot to put the button
	 * @param button the {@link ButtonBase} to set
	 */
	protected void setButton(final int slot, final ButtonBase button) {
		if (button == null) {
			this.buttons.remove(slot);
		} else {
			this.buttons.put(slot, button);
		}
	}


	/**
	 * Refills the inventory and updates all viewers with changes.
	 */
	public CompletableFuture<MenuBase> updateInventory() {
		return FutureHandler.async.supply(() -> {
			generateInventory();

			refillInventory();

			getInventory().getViewers().forEach(humanEntity -> {
				if (humanEntity instanceof Player) {
					((Player) humanEntity).updateInventory();
				}
			});

			return this;
		});
	}

	/**
	 * Creates an inventory if there isn't one already.
	 */
	protected CompletableFuture<MenuBase> generateInventory() {
		if (getInventory() != null) {
			return FutureHandler.async.supply(() -> this);
		}

		return regenerateInventory();
	}

	/**
	 * Creates an inventory.
	 */
	protected CompletableFuture<MenuBase> regenerateInventory() {
		synchronized (this.inventoryLock) {
			this.inventory = Bukkit.createInventory(null, this.size, this.title);
		}

		return refillInventory();
	}

	/**
	 * Fills the inventory with buttons.
	 */
	@SneakyThrows
	protected CompletableFuture<MenuBase> refillInventory() {
		if (getInventory() == null) {
			return FutureHandler.async.supply(() -> {
				try {
					return generateInventory().get().refillInventory().get();
				} catch (final InterruptedException | ExecutionException e) {
					throw new RuntimeException(MENU_ERROR_MESSAGE, e);
				}
			});
		}

		return FutureHandler.async.supply(() -> {
			getInventory().clear();

			if (this.enablePreviousMenuButton) {
				if (this.parent != null) {
					setButton(this.previousMenuButtonSlot, new ActionButton(this.previousMenuButton.getItem(), ((event, clicker, clicked) -> {
						this.parent.displayTo(clicker);
					})));
				}
			}

			for (final Map.Entry<Integer, ButtonBase> entry : this.buttons.entrySet()) {
				final Integer    slot   = entry.getKey();
				final ButtonBase button = entry.getValue();

				getInventory().setItem(slot, button.getItem());
			}

			return this;
		});
	}

	/**
	 * Displays the menu to a player.
	 *
	 * @param player the player to display the menu to.
	 */
	@SneakyThrows
	public void displayTo(@NonNull final Player player) {
		generateInventory().get();
		final MenuBase current = getCurrentMenu(player);

		if (current != null) {
			TaskManager.sync.later(() -> player.setMetadata(NBT_PREVIOUS_MENU, new FixedMetadataValue(Checks.instanceCheck(), current)));
		}

		TaskManager.sync.later(() -> player.setMetadata(NBT_CURRENT_MENU, new FixedMetadataValue(Checks.instanceCheck(), this)));

		onOpen(player, current);

		player.openInventory(getInventory());
	}

	/**
	 * Called upon opening of this menu to player.
	 *
	 * @param player   the player that this menu was opened to
	 * @param previous the previous menu the player was viewing, or {@code null}
	 */
	protected void onOpen(@NonNull final Player player, final MenuBase previous) {}

	/**
	 * Called the closing of this menu.
	 *
	 * @param event the event
	 */
	protected void onClose(@NonNull final InventoryCloseEvent event) {}

	/**
	 * Called upon a click to any slot in this menu.
	 *
	 * @param event the event
	 */
	protected void onGenericClick(@NonNull final InventoryClickEvent event) {}

	public Inventory getInventory() {
		synchronized (this.inventoryLock) {
			return this.inventory;
		}
	}
}
