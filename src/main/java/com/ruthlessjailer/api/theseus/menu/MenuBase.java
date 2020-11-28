package com.ruthlessjailer.api.theseus.menu;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
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
	protected final     Map<Integer, Button> buttons           = new HashMap<>();
	private final       MenuBase             parent;
	private final       String               title;
	private final       int                  size;
	private final       InventoryType        type;
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

	public static MenuBase getCurrentMenu(@NonNull final Player player) {
		return getMenu(player, NBT_CURRENT_MENU);
	}

	public static MenuBase getPreviousMenu(@NonNull final Player player) {
		return getMenu(player, NBT_PREVIOUS_MENU);
	}

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

	protected final void setButton(final int slot, @NonNull final Button button) {
		this.buttons.put(slot, button);
	}

	protected final void updateInventory() {
		generateInventory();

		refillInventory();

		getInventory().getViewers().forEach(humanEntity -> {
			if (humanEntity instanceof Player) {
				((Player) humanEntity).updateInventory();
			}
		});
	}

	protected final void generateInventory() {
		if (this.inventory != null) {
			return;
		}

		regenerateInventory();
	}

	protected final void regenerateInventory() {
		this.inventory = Bukkit.createInventory(null, this.size, this.title);

		refillInventory();
	}

	protected final void refillInventory() {
		generateInventory();

		this.inventory.clear();

		for (final Map.Entry<Integer, Button> entry : this.buttons.entrySet()) {
			final Integer slot   = entry.getKey();
			final Button  button = entry.getValue();

			this.inventory.setItem(slot, button.getItem());
		}
	}

	public final void displayTo(@NonNull final Player player) {
		generateInventory();

		final MenuBase menu = getCurrentMenu(player);

		if (menu != null) {
			Common.runLater(() -> player.setMetadata(NBT_PREVIOUS_MENU, new FixedMetadataValue(Checks.instanceCheck(), menu)));
		}

		Common.runLater(() -> player.setMetadata(NBT_CURRENT_MENU, new FixedMetadataValue(Checks.instanceCheck(), this)));

		player.openInventory(this.inventory);
	}

	protected void onOpen(@NonNull final Player player, final MenuBase previous) {}

	protected void onClose(@NonNull final InventoryCloseEvent event)             {}

	protected void onGenericClick(@NonNull final InventoryClickEvent event)      {}


}
