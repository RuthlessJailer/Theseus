package com.ruthlessjailer.api.theseus.menu;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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
public abstract class MenuBase implements Listener {

	public static final String               NBT_CURRENT_MENU  = "THESEUS_CURRENT_MENU";
	public static final String               NBT_PREVIOUS_MENU = "THESEUS_PREVIOUS_MENU";
	public static final int                  MAX_SLOTS         = 54;
	protected final     Map<Integer, Button> buttons           = new HashMap<>();
	private final       MenuBase             parent;
	private final       String               title;
	private final       int                  size;
	private final       InventoryType        type;
	@Setter(AccessLevel.PROTECTED)
	private             boolean              enableBackButton  = true;
	private             Inventory            inventory;

	public MenuBase(@NonNull final InventoryType type, @NonNull final String title) {
		this(null, type, title);
	}

	public MenuBase(final int size, @NonNull final String title) {
		this(null, size, title);
	}

	public MenuBase(final MenuBase parent, final int size, @NonNull final String title) {
		this.parent = parent;
		this.size   = size;
		this.title  = Chat.colorize(title);
		this.type   = null;

		Bukkit.getPluginManager().registerEvents(this, Checks.instanceCheck(String.format(
				"Plugin instance cannot be null when initializing menu listener %s.",
				ReflectUtil.getPath(this.getClass()))));
	}

	public MenuBase(final MenuBase parent, @NonNull final InventoryType type, @NonNull final String title) {
		this.parent = parent;
		this.size   = 0;
		this.title  = Chat.colorize(title);
		this.type   = type;

		Bukkit.getPluginManager().registerEvents(this, Checks.instanceCheck(String.format(
				"Plugin instance cannot be null when initializing menu listener %s.",
				ReflectUtil.getPath(this.getClass()))));
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
	 * Sets a button.
	 *
	 * @param slot   the slot to put the button
	 * @param button the {@link Button} to set
	 */
	protected void setButton(final int slot, @NonNull final Button button) {
		this.buttons.put(slot, button);
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

		if (this.enableBackButton) {
			if (this.parent != null) {
				setButton(0, new Button(new ItemStack(Material.ARROW), ((event, clicker, clicked) -> {
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
