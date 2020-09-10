package com.ruthlessjailer.api.theseus.item;


import com.ruthlessjailer.api.theseus.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vadim Hagedorn
 */
public final class BookUtil {//TODO: use reflection to exclude version-dependent imports

	private final Material     material;
	private final List<String> pages  = new ArrayList<>();
	private       String       title;
	private       String       author = "RuthlessJailer";
	private       String       name   = "Book";
	private       List<String> lore   = new ArrayList<>();


	private BookUtil(final Material material, final String title) {
		this.material = material;
		this.title    = Chat.colorize(title);
	}

	public static BookUtil writtenBook(final String author, final String title) {
		return new BookUtil(Material.WRITTEN_BOOK, title);
	}

	public static BookUtil unsignedBook(final String author, final String title) {
		return new BookUtil(Material.WRITABLE_BOOK, title);
	}


	public BookUtil name(final String name) {
		this.name = Chat.colorize(name);
		return this;
	}

	public BookUtil addLore(final String... lore) {
		for (final String line : lore) {
			this.lore.add(Chat.colorize(line));
		}
		return this;
	}

	public BookUtil lore(final String... lore) {
		this.lore = new ArrayList<>();
		for (final String line : lore) {
			this.lore.add(Chat.colorize(line));
		}
		return this;
	}

	public BookUtil title(final String title) {
		this.title = Chat.colorize(title);
		return this;
	}

	public BookUtil author(final String name) {
		this.author = Chat.colorize(name);
		return this;
	}

	public BookUtil addPage(final String... lines) {
		final StringBuilder page = new StringBuilder();
		for (final String line : lines) {
			page.append(Chat.colorize(line));
		}
		this.pages.add(page.toString());
		return this;
	}

	public void displayTo(final Player player) {
//		int slot = player.getInventory().getHeldItemSlot();
//		ItemStack original = player.getInventory().getItem(slot);
//		player.getInventory().setItem(slot, this.make());
//
//		ByteBuf buf = Unpooled.buffer(256);
//		buf.setByte(0, (byte) 1);
//		buf.writerIndex(1);
//
//		PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(MinecraftKey.a("MC|BOpen"), new PacketDataSerializer(buf));
//		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//
//		player.getInventory().setItem(slot, original);
	}

	public ItemBuilder.ItemStackCreator build() {
		final ItemStack item = new ItemStack(this.material);
		final BookMeta  meta = (BookMeta) item.getItemMeta();

		meta.setTitle(this.title);
		meta.setAuthor(this.author);
		meta.setDisplayName(this.name);
		meta.setPages(this.pages);
		meta.setLore(this.lore);

		item.setItemMeta(meta);
		return ItemBuilder.of(item);
	}

	public ItemStack make() {
		final ItemStack item = new ItemStack(this.material);
		final BookMeta  meta = (BookMeta) item.getItemMeta();

		meta.setTitle(this.title);
		meta.setAuthor(this.author);
		meta.setDisplayName(this.name);
		meta.setPages(this.pages);
		meta.setLore(this.lore);

		item.setItemMeta(meta);
		return item;
	}

}
