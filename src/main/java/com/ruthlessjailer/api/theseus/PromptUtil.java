package com.ruthlessjailer.api.theseus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author RuthlessJailer
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class PromptUtil implements Listener {

	public static String exitMessage = "exit";

	@Getter(AccessLevel.PUBLIC)
	private static final Listener listenerInstance = new PromptUtil("", Collections.emptyList(), new String[0]);

	private final String       text;
	private final List<String> pages;
	private final String[]     lines;

	public static void chat(@NonNull final Player player, @NonNull final Consumer<PromptUtil> consumer) {
		final PlayerCache cache = PlayerCache.getCache(player);
		cache.addChatConsumer((txt) -> consumer.accept(new PromptUtil(txt, Collections.emptyList(), new String[0])));
	}

	//book editor is client-side :(
	public static <E extends Enum<E>> void book(@NonNull final Player player, @NonNull final Consumer<PromptUtil> consumer) {
		final PlayerCache cache = PlayerCache.getCache(player);
		cache.addBookConsumer((pages) -> consumer.accept(new PromptUtil("", pages, new String[0])));
		final ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
		final BookMeta  meta = (BookMeta) book.getItemMeta();
		meta.addPage("yeet");
		book.setItemMeta(meta);
		final ItemStack og   = player.getInventory().getItemInHand();
		final int       slot = player.getInventory().getHeldItemSlot();
		player.getInventory().setItem(slot, book);
//		final Object packet = newInstanceOf(getNMSClass("PacketPlayOutOpenBook"), getEnum((Class<E>) getNMSClass("EnumHand"), "MAIN_HAND"));
//		Common.runLater(() -> {//((CraftPlayer)player).getHandle().playerConnection.sendPacket()
//			sendPacket(player, packet);
		player.getInventory().setItem(slot, og);
//		});
	}

	public static void sign(@NonNull final Player player, final Sign sign, @NonNull final Consumer<PromptUtil> consumer) {
		final PlayerCache cache = PlayerCache.getCache(player);

		final Location blockLoc = sign.getLocation();

//		final Object blockPos = newInstanceOf(getConstructor(getNMSClass("BlockPosition"), int.class, int.class, int.class),
//											  blockLoc.getBlockX(), blockLoc.getBlockY(), blockLoc.getBlockZ());
//		final Object packet = newInstanceOf(getConstructor(getNMSClass("PacketPlayOutOpenSignEditor"), blockPos.getClass()), blockPos);


		cache.addSignConsumer((lines) -> {
			consumer.accept(new PromptUtil("", Collections.emptyList(), lines));
		});

		sign.setEditable(true);
		sign.update(true);

//		sendPacket(player, packet);
	}

	private static void sendPacket(@NonNull final Player player, @NonNull final Object packet) {
		//((CraftPlayer)player).getHandle().playerConnection.sendPacket()
//		final Object nmsPlayer        = invokeMethod(getOBCClass("entity.CraftPlayer"), "getHandle", player);
//		final Object playerConnection = getFieldValue(getNMSClass("EntityPlayer"), "playerConnection", nmsPlayer);
//		invokeMethod(getMethod(playerConnection.getClass(), "sendPacket", getNMSClass("Packet")), playerConnection, packet);
	}

	@EventHandler
	public void onChat(final AsyncPlayerChatEvent event) {
		final PlayerCache cache = PlayerCache.getCache(event.getPlayer());
		if (cache.isWaitingToChat()) {
			if (event.getMessage().equals(exitMessage)) {
				cache.getChatConsumer().clear();
				event.setCancelled(true);
				return;
			}
			cache.getChatConsumer().poll().accept(event.getMessage());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBook(final PlayerEditBookEvent event) {
		final PlayerCache cache = PlayerCache.getCache(event.getPlayer());
		if (cache.isWaitingToBook()) {
			cache.getBookConsumer().poll().accept(event.getNewBookMeta().getPages());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSign(final SignChangeEvent event) {
		final PlayerCache cache = PlayerCache.getCache(event.getPlayer());
		if (cache.isWaitingToSign()) {
			cache.getSignConsumer().poll().accept(event.getLines());
			event.setCancelled(true);
		}
	}
}