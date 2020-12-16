package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import com.ruthlessjailer.api.theseus.menu.button.ActionButton;
import com.ruthlessjailer.api.theseus.menu.button.MenuButton;
import com.ruthlessjailer.api.theseus.multiversion.XMaterial;
import com.ruthlessjailer.api.theseus.task.handler.FutureHandler;
import com.ruthlessjailer.api.theseus.task.manager.TaskManager;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author RuthlessJailer
 */
public class InvMenu extends MenuBase implements Listener {

	private static final Map<UUID, InvMenu> inventories = new HashMap<>();
	private final        UUID               player;
	private              boolean            enabled     = true;

	public static InvMenu getMenu(@NonNull final Player player) {
		InvMenu menu = inventories.get(player.getUniqueId());

		if (menu == null) {
			menu = new InvMenu(player);
			inventories.put(player.getUniqueId(), menu);
		}

		return menu;
	}

	private InvMenu(@NonNull final Player player) {
		super(MAX_SLOTS, "&9" + player.getName() + "&e's Inventory");

		this.player = player.getUniqueId();

		setProtectEmptySlots(false);

		Bukkit.getPluginManager().registerEvents(this, Checks.instanceCheck());
//		final ButtonAction action = (event, clicker, clicked) -> {
//			event.setResult(Event.Result.ALLOW);
//			event.setCancelled(false);
//			final int       slot      = event.getSlot();
//			final Inventory inventory = event.getClickedInventory();
//
//			System.out.println(getPlayer().getInventory().getItem(slot));
//			System.out.println(inventory.getItem(slot));
//
//			TaskManager.sync.delay(() -> {
//				getPlayer().getInventory().setItem(slot, inventory.getItem(slot));
//				getPlayer().updateInventory();
//			});
//		};

		setButton(41, new ActionButton(ItemBuilder.of(XMaterial.BLACK_STAINED_GLASS_PANE.toItemStack()).name("&1&l&m<-&r &4Armor &2and &3Offhand &2slots.").build().create()));

		final String[] names = Common.asArray("&4Boots", "&4Leggings", "&4Chestplate", "&4Helmet", "&3Offhand");
		ArrayUtils.reverse(names);
		boolean menu = false;

		for (int i = 41; i < MAX_SLOTS; i++) {
			final ItemBuilder.ItemStackCreator builder = ItemBuilder.of(XMaterial.BLACK_STAINED_GLASS_PANE.toItemStack());
			if (i == 41) {
				builder.name("&1&l&m<-&r &4Armor &2and &3Offhand &2slots.");
			} else if (i < 45) {
				builder.name(" ");
			} else if (i < 50) {
				builder.name(names[(50 - i) - 1]);
			} else if (i < 53) {
				builder.name(" ");
			} else {
				builder.material(Material.ENDER_CHEST).name("&1Ender Chest");
				menu = true;
			}

			if (menu) {
				setButton(i, new MenuButton(builder.build().create(), EchestMenu.getMenu(player)));
			} else {
				setButton(i, new ActionButton(builder.build().create()));
			}
		}

		updateInventory();
//		TaskManager.async.repeat(() -> {
//			try {
//				for (int i = 0; i < getPlayer().getInventory().getSize(); i++) {
//					updateInventory().get();
//					getPlayer().getInventory().setItem(i, getInventory().getItem(i));
//				}
//			} catch (final Throwable t) {
//				t.printStackTrace();
//			}
//		}, 1);


//		final ItemStack nullItem = new ItemStack(Material.AIR);
//
//		for (int i = 0; i < player.getInventory().getSize(); i++) {
//			final ItemStack  item   = player.getInventory().getItem(i);
//			final ButtonBase button = new ActionButton(item == null ? nullItem : item, action);
//			button.setProtect(false);
//			setButton(i, button);
//		}
	}

	@Override
	protected void onGenericClick(final @NonNull InventoryClickEvent event) {
		event.setResult(Event.Result.ALLOW);
		event.setCancelled(false);

		final int       slot = event.getSlot();
		final ItemStack item = getInventory().getItem(slot) == null ? new ItemStack(Material.AIR) : getInventory().getItem(slot).clone();
		TaskManager.sync.delay(() -> {
			getPlayer().getInventory().setItem(slot, item);
			updatePlayerInventory();
		}, 1);
	}

	private void updatePlayerInventory() {//updates player inventory based on what's in gui
		for (int i = 0; i < getPlayer().getInventory().getSize(); i++) {
			getPlayer().getInventory().setItem(i, getInventory().getItem(i));
		}
		getPlayer().updateInventory();
	}

	@Override
	@SneakyThrows
	public CompletableFuture<MenuBase> updateInventory() {//updates based on what player has in his inventory
		generateInventory().get();
		return FutureHandler.async.supply(() -> {
			for (int i = 0; i < getPlayer().getInventory().getSize(); i++) {
				getInventory().setItem(i, getPlayer().getInventory().getItem(i));
			}

			getPlayer().updateInventory();

			return this;
		});
	}

	@EventHandler
	public void onPlayerClick(final InventoryClickEvent event) {
		if (!this.enabled) {//stop working when closed
			return;
		}
		if (event.getWhoClicked().getUniqueId().equals(this.player)) {//update inventory for viewer and not player
			if (event.getView().getBottomInventory().equals(event.getClickedInventory())) {
				updateInventory();
			}
		}
	}

	@Override
	protected void onOpen(final @NonNull Player player, final MenuBase previous) {
		this.enabled = true;
	}

	@Override
	protected void onClose(final @NonNull InventoryCloseEvent event) {
		this.enabled = false;
	}

	public Player getPlayer() {
		return Bukkit.getOfflinePlayer(this.player).getPlayer();
	}

	public static class EchestMenu extends MenuBase {

		private static final Map<UUID, EchestMenu> inventories = new HashMap<>();

		EchestMenu(@NonNull final Player player) {
			super(9 * 5, "&9" + player.getName() + "&e's Ender Chest");

			setEnablePreviousMenuButton(false);


		}

		public static EchestMenu getMenu(@NonNull final Player player) {
			EchestMenu menu = inventories.get(player.getUniqueId());

			if (menu == null) {
				menu = new EchestMenu(player);
				inventories.put(player.getUniqueId(), menu);
			}

			return menu;
		}
	}

}
