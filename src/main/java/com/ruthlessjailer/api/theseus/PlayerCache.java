package com.ruthlessjailer.api.theseus;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author RuthlessJailer
 */
@Getter
@Setter
final class PlayerCache {

	private static final Map<UUID, PlayerCache> cache = new HashMap<>();

	private final Queue<Consumer<String>>       chatConsumer = new LinkedList<>();
	private final Queue<Consumer<List<String>>> bookConsumer = new LinkedList<>();
	private final Queue<Consumer<String[]>>     signConsumer = new LinkedList<>();

	public void addChatConsumer(@NonNull final Consumer<String> consumer) {
		this.chatConsumer.add(consumer);
	}

	public void addBookConsumer(@NonNull final Consumer<List<String>> consumer) {
		this.bookConsumer.add(consumer);
	}

	public void addSignConsumer(@NonNull final Consumer<String[]> consumer) {
		this.signConsumer.add(consumer);
	}

	public boolean isWaitingToChat() {
		return !this.chatConsumer.isEmpty();
	}

	public boolean isWaitingToBook() {
		return !this.bookConsumer.isEmpty();
	}

	public boolean isWaitingToSign() {
		return !this.signConsumer.isEmpty();
	}


	public static PlayerCache getCache(@NonNull final Player player) {

		PlayerCache pcache = cache.get(player.getUniqueId());

		if (pcache == null) {
			pcache = new PlayerCache();
			cache.put(player.getUniqueId(), pcache);
		}

		return pcache;
	}

}
