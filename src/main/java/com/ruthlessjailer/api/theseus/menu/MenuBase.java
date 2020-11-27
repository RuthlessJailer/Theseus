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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Only ONE player per instance.
 * If {@link MenuBase#displayTo(Player)} is called twice, a new instance will be created and returned will be thrown.
 *
 * @author RuthlessJailer
 */
@Getter
public abstract class MenuBase implements Listener {

	public static final String NBT_CURRENT_MENU  = "THESEUS_CURRENT_MENU";
	public static final String NBT_PREVIOUS_MENU = "THESEUS_PREVIOUS_MENU";

	protected final Map<Integer, Button> buttons = new HashMap<>();

	private final MenuBase      parent;
	private final String        title;
	private final int           size;
	private final InventoryType type;

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

	private static MenuBase getMenu(@NonNull final Player player, @NonNull final String tag) {
		if (!player.hasMetadata(tag)) {
			return null;
		}

		final MenuBase menu = (MenuBase) player.getMetadata(tag).get(0).value();
		Checks.nullCheck(menu, "Player " + player.getName() + " is missing metadata tag value " + tag + "; cannot retrieve the menu.");
		return menu;
	}

	protected final void addButton(final int slot, @NonNull final Button button) {
		this.buttons.put(slot, button);
	}

	public final MenuBase displayTo(@NonNull final Player player) {
		final Inventory inventory = Bukkit.createInventory(null, this.size, this.title);

		for (final Map.Entry<Integer, Button> entry : this.buttons.entrySet()) {
			final Integer slot   = entry.getKey();
			final Button  button = entry.getValue();

			inventory.setItem(slot, button.getItem());
		}

//		player.openInventory(view);

		return this;
	}

	protected void onOpen(@NonNull final Player player, final MenuBase previous) {}

	protected void onClose(@NonNull final InventoryCloseEvent event)             {}

	protected void onGenericClick(@NonNull final InventoryClickEvent event)      {}

	@EventHandler
	public void onCloseEvent(@NonNull final InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getPlayer();

		final MenuBase current = getCurrentMenu(player);

		if (current == null) {
			return;
		}

		player.removeMetadata(NBT_CURRENT_MENU, Checks.instanceCheck());
		player.setMetadata(NBT_PREVIOUS_MENU, new FixedMetadataValue(Checks.instanceCheck(), this));
	}

	@EventHandler
	public void onClick(@NonNull final InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getWhoClicked();

		final MenuBase current = getCurrentMenu(player);

		if (current == null) {
			return;
		}

		//now we know it's the right menu

		onGenericClick(event);

		final Button clicked = this.buttons.get(event.getSlot());

		if (clicked == null) {
			return;
		}

		if (!clicked.getItem().isSimilar(event.getCurrentItem())) {
			return;
		}

		//it's the right item

		switch (clicked.getType()) {
			case INFO:
				event.setCancelled(true);
				event.setCurrentItem(null);
				break;
			case TAKE:
				event.setCancelled(false);
				break;
			case ACTION:
				Checks.nullCheck(clicked.getAction(), "ButtonAction cannot be null as its type is ACTION!");
				clicked.getAction().onClick(event, (Player) event.getWhoClicked(), event.getClick(), event.getCurrentItem());
		}
	}

}
