package com.ruthlessjailer.api.theseus.menu;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RuthlessJailer
 */
@Getter
public abstract class MenuBase {

	public static final String               NBT_CURRENT_MENU         = "THESEUS_CURRENT_MENU";
	public static final String               NBT_PREVIOUS_MENU        = "THESEUS_PREVIOUS_MENU";
	public static final int                  MAX_SLOTS                = 54;
	public static final int                  MIN_SLOTS                = 9;
	protected final     Map<Integer, Button> buttons                  = new HashMap<>();
	private final       MenuBase             parent;
	private final       String               title;
	private final       int                  size;
	private final       InventoryType        type;
	private             int                  previousMenuButtonSlot   = 0;
	private             Button               previousMenuButton;
	@Setter(AccessLevel.PROTECTED)
	private             boolean              enablePreviousMenuButton = true;
	private             Inventory            inventory;

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

		setPreviousMenuButton(ItemBuilder.of(Material.ARROW, "&9&l&m<-").build().create());
	}

	public MenuBase(final MenuBase parent, @NonNull final InventoryType type, @NonNull final String title) {
		this.parent = parent;
		this.size   = 0;
		this.title  = Chat.colorize(title);
		this.type   = type;

		setPreviousMenuButton(ItemBuilder.of(Material.ARROW, "&9&l&m<-").build().create());
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
		this.previousMenuButton = new Button(item, (this.parent == null ? ButtonAction.EMPTY_ACTION : (event, clicker, clicked) -> this.parent.displayTo(clicker)));
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
	 * @param button the {@link Button} to set
	 */
	protected void setButton(final int slot, final Button button) {
		if (button == null) {
			this.buttons.remove(slot);
		} else {
			this.buttons.put(slot, button);
		}
	}


	/**
	 * Refills the inventory and updates all viewers with changes.
	 */
	protected void updateInventory() {
		generateInventory();

		refillInventory();

		getInventory().getViewers().forEach(humanEntity -> {
			if (humanEntity instanceof Player) {
				((Player) humanEntity).updateInventory();
			}
		});
	}

	/**
	 * Creates an inventory if there isn't one already.
	 */
	protected void generateInventory() {
		if (this.inventory != null) {
			return;
		}

		regenerateInventory();
	}

	/**
	 * Creates an inventory.
	 */
	protected void regenerateInventory() {
		this.inventory = Bukkit.createInventory(null, this.size, this.title);

		refillInventory();
	}

	/**
	 * Fills the inventory with buttons.
	 */
	protected void refillInventory() {
		generateInventory();

		this.inventory.clear();

		if (this.enablePreviousMenuButton) {
			if (this.parent != null) {
				setButton(this.previousMenuButtonSlot, new Button(this.previousMenuButton.getItem(), ((event, clicker, clicked) -> {
					this.parent.displayTo(clicker);
				})));
			}
		}

		for (final Map.Entry<Integer, Button> entry : this.buttons.entrySet()) {
			final Integer slot   = entry.getKey();
			final Button  button = entry.getValue();

			this.inventory.setItem(slot, button.getItem());
		}
	}

	/**
	 * Displays the menu to a player.
	 *
	 * @param player the player to display the menu to.
	 */
	public void displayTo(@NonNull final Player player) {
		generateInventory();

		final MenuBase menu = getCurrentMenu(player);

		if (menu != null) {
			Common.runLater(() -> player.setMetadata(NBT_PREVIOUS_MENU, new FixedMetadataValue(Checks.instanceCheck(), menu)));
		}

		Common.runLater(() -> player.setMetadata(NBT_CURRENT_MENU, new FixedMetadataValue(Checks.instanceCheck(), this)));

		onOpen(player, menu);

		player.openInventory(this.inventory);
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
	protected void onGenericClick(@NonNull final InventoryClickEvent event)      {}


}
