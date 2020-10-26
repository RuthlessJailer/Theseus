package com.ruthlessjailer.api.theseus.item;


import com.ruthlessjailer.api.theseus.Chat;
import lombok.Builder;
import lombok.Singular;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

/**
 * @author Vadim Hagedorn
 */
@Builder(builderClassName = "BookBuilderCreator")
public final class BookBuilder {//TODO: use reflection to exclude version-dependent imports

	private final Material     material;
	@Singular
	private final List<String> pages;
	private final String       title;
	private final String       author;
	private final String       name;
	private final List<String> lore;

	public static final class BookBuilderCreator {

		public BookBuilderCreator lore(final String lore) {
			this.lore.add(Chat.colorize(lore));
			return this;
		}

		public BookBuilderCreator lores(final List<String> lores) {
			lores.forEach(this::lore);
			return this;
		}

		public ItemStack build() {
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

	@Builder
	public static final class Page {

		private final List<String>    lines;
		private final BaseComponent[] components;


	}

}
