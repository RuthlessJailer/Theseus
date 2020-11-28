package com.ruthlessjailer.api.theseus.menu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Only ONE player per instance.
 * If {@link MenuBase#displayTo(Player)} is called twice, a new instance will be created and returned will be thrown.
 *
 * @author RuthlessJailer
 */
@Getter
public abstract class MenuBase implements Listener {

	public static final  String               NBT_CURRENT_MENU  = "THESEUS_CURRENT_MENU";
	public static final  String               NBT_PREVIOUS_MENU = "THESEUS_PREVIOUS_MENU";
	private static final Gson                 GSON              = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(MenuBase.class, new MenuBaseAdapter())
			.registerTypeAdapter(ButtonAction.class, new ButtonActionAdapter())
			.create();
	protected final      Map<Integer, Button> buttons           = new HashMap<>();
	private final        MenuBase             parent;
	private final        String               title;
	private final        int                  size;
	private final        InventoryType        type;

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

	@SneakyThrows
	private static MenuBase getMenu(@NonNull final Player player, @NonNull final String tag) {
		if (!player.hasMetadata(tag)) {
			return null;
		}

		final List<MetadataValue> meta = player.getMetadata(tag);

		if (meta.isEmpty() || meta.get(0).value() == null) {
			Chat.warning("Player " + player.getName() + "'s metadata " + tag + " is corrupted; cannot retrieve the menu.");
			return null;
		}

		final String serialized = GSON.toJson(meta.get(0).value());

		System.out.println(serialized);

		final MenuBase menu = GSON.fromJson(serialized, MenuBase.class);//serialize and deserialize because of classloader issue

		Checks.nullCheck(menu, "Player " + player.getName() + " is missing metadata tag value " + tag + "; cannot retrieve the menu.");

		return menu;
	}

//	@SneakyThrows
//	private static String serializeButtonBase(@NonNull final MenuBase menu) {
//		final ByteArrayOutputStream out = new ByteArrayOutputStream();
//		final ObjectOutputStream object = new ObjectOutputStream(out);
//
//		object.write(action);
//	}

	protected final void addButton(final int slot, @NonNull final Button button) {
		this.buttons.put(slot, button);
	}

	public final void displayTo(@NonNull final Player player) {
		final Inventory inventory = Bukkit.createInventory(null, this.size, this.title);

		System.out.println(inventory.getSize());
		System.out.println(inventory.firstEmpty());

		for (final Map.Entry<Integer, Button> entry : this.buttons.entrySet()) {
			final Integer slot   = entry.getKey();
			final Button  button = entry.getValue();

			inventory.setItem(slot, button.getItem());
		}

		final MenuBase menu = getCurrentMenu(player);

		if (menu != null) {
			player.setMetadata(NBT_PREVIOUS_MENU, new MenuMetadataValue(menu));
		}

		player.setMetadata(NBT_CURRENT_MENU, new MenuMetadataValue(this));
		player.openInventory(inventory);
	}

	protected void onOpen(@NonNull final Player player, final MenuBase previous) {}

	protected void onClose(@NonNull final InventoryCloseEvent event)             {}

	protected void onGenericClick(@NonNull final InventoryClickEvent event)      {}


}
