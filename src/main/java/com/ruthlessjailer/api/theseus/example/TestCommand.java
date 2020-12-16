package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.PromptUtil;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import com.ruthlessjailer.api.theseus.command.CommandBase;
import com.ruthlessjailer.api.theseus.command.SubCommand;
import com.ruthlessjailer.api.theseus.command.SuperiorCommand;
import com.ruthlessjailer.api.theseus.io.update.Updater;
import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import com.ruthlessjailer.api.theseus.multiversion.MinecraftVersion;
import com.ruthlessjailer.api.theseus.multiversion.XColor;
import com.ruthlessjailer.api.theseus.multiversion.XMaterial;
import com.ruthlessjailer.api.theseus.task.handler.FutureHandler;
import com.ruthlessjailer.api.theseus.task.manager.TaskManager;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RuthlessJailer
 */
public class TestCommand extends CommandBase implements SuperiorCommand {

	static TestMenu test;

	static {
		if (MinecraftVersion.atLeast(MinecraftVersion.v1_12)) {
			test = new TestMenu();
		}
	}

	public TestCommand() {
		super("test");
	}

	@Override
	protected void runCommand(@NonNull final CommandSender sender, final String[] args, @NonNull final String label) {
	}

	@SubCommand(inputArgs = "color %i")
	private void color(final CommandSender sender, final String[] args, final Integer color) {
		final XColor converted = XColor.fromColor(new Color(color));
		Chat.send(sender, converted + converted.name());
	}

	@SubCommand(inputArgs = "material %s")
	private void material(final CommandSender sender, final String[] args, final String material) {
		final XMaterial x = XMaterial.getXMaterial(joinArgs(1, args).replaceAll(" ", "_").toUpperCase());

		Chat.send(sender, x.name());
		Chat.send(sender, x.toItemStack().getData().toString());

		if (sender instanceof Player) {
			getPlayer(sender).getInventory().addItem(x.toItemStack());
		}

	}

	@SubCommand(inputArgs = "item %e", argTypes = Material.class)
	private void item(final CommandSender sender, final String[] args, final Material material) {
		getPlayer(sender).getInventory().addItem(ItemBuilder.of(material).build().create());
	}

	@SubCommand(inputArgs = "menu")
	private void menu(final CommandSender sender, final String[] args) {
//		System.out.println("yeeting:");
//		try {
//			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			final ObjectOutputStream    oos  = new ObjectOutputStream(baos);
//			oos.writeObject((ButtonAction & Serializable) (event, clicker, click) -> {
//				System.out.println("yEEEEEEEEEEEEEEEEEEEEEE");
//			});
//
//			final byte[] bytes = baos.toByteArray();
//
//			System.out.println(new String(bytes));
//			oos.close();
//			baos.close();
//
//			final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//			final ObjectInputStream    ois  = new ObjectInputStream(bais);
//
//			final ButtonAction deserialized = (ButtonAction) ois.readObject();
//			deserialized.onClick(null, null, null);
//		} catch (final Exception e) {
//			System.err.println("motherfucker threw an exception");
//			e.printStackTrace();
//		}
//		System.out.println("yeeted");

		test.displayTo(getPlayer(sender));
		System.out.println("display'd");
	}

	@SubCommand(inputArgs = "ghost %p<Player> %b<All>")
	private void place(final CommandSender sender, final String[] args, final OfflinePlayer player, final boolean all) {
		if (TestListener.GHOSTING.containsKey(player.getUniqueId())) {
			TestListener.GHOSTING.remove(player.getUniqueId());
			Chat.send(sender, "&4No longer ghosting &b" + player.getName());
		} else {
			TestListener.GHOSTING.put(player.getUniqueId(), all);
			Chat.send(sender, "&2Now ghosting &b" + player.getName());
		}
	}

	@SubCommand(inputArgs = "update")
	private void update(final CommandSender sender, final String[] args) {
		final Updater updater = new Updater(86123);
		System.out.println(updater.isValidResource());
		updater.canUpdate();
	}

	@SubCommand(inputArgs = "invsee %p")
	private void invsee(final CommandSender sender, final String[] args, final OfflinePlayer target) {
		final Player player = getPlayer(sender);
		if (target == null || !target.isOnline() || target.getPlayer() == null) {
			Chat.send(sender, "&b" + (target == null ? "INVALID PLAYER" : target.getName()) + "&c is not online.");
			return;
		}

		InvMenu.getMenu(target.getPlayer()).displayTo(player);
	}

	@SubCommand(inputArgs = "chat")
	private void chat(final CommandSender sender, final String[] args) {
		Chat.send(getPlayer(sender), "&3Send a message boi");
		PromptUtil.chat(getPlayer(sender), (p) -> {
			Chat.send(sender, "&aYou said '&r" + p.getText() + "&a'.");
		});
	}

	@SubCommand(inputArgs = "book")
	private void book(final CommandSender sender, final String[] args) {
		Chat.send(getPlayer(sender), "&3Send a message boi");
		PromptUtil.book(getPlayer(sender), (p) -> {
			Chat.send(sender, "&aYou said '&r" + p.getPages() + "&a'.");
		});
	}

	@SubCommand(inputArgs = "task")
	@SneakyThrows
	private void task(final CommandSender sender, final String[] args) {
		TaskManager.sync.later(() -> System.out.println("sync later (task)"));
		TaskManager.sync.delay(() -> System.out.println("sync run (task)"), 10);
		TaskManager.async.repeat(new BukkitRunnable() {
			int i = 0;

			@Override
			public void run() {
				if (this.i > 5) {
					cancel();
				}
				System.out.println("sync repeat (task) " + this.i++);
			}
		}, 20);
		TaskManager.async.later(() -> System.out.println("async later (task)"));
		TaskManager.async.delay(() -> System.out.println("async run (task)"), 30);
		TaskManager.async.repeat(new BukkitRunnable() {
			int i = 0;

			@Override
			public void run() {
				if (this.i > 5) {
					cancel();
				}
				System.out.println("async repeat (task) " + this.i++);
			}
		}, 40);

		TaskManager.async.later(() -> {
			try {
				FutureHandler.sync.supply(() -> {
					final Chunk chunk = Bukkit.getWorlds().get(0).getChunkAt(0, 0);
					System.out.println("sync later (future) " + chunk.getX() + ", " + chunk.getZ());
					return chunk;
				}).get();

				FutureHandler.sync.supply(() -> {
					final Chunk chunk = Bukkit.getWorlds().get(0).getChunkAt(0, 0);
					System.out.println("sync run (future) " + chunk.getX() + ", " + chunk.getZ());
					return chunk;
				}, 50).get();
			} catch (final InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});


		FutureHandler.async.supply(() -> Bukkit.getOfflinePlayer("ruthlessjailer")).thenAccept((op) -> {
			System.out.println("async later (future) " + op.getName());
		});

		FutureHandler.async.supply(() -> Bukkit.getOfflinePlayer("ruthlessjailer"), 6000).thenAccept((op) -> {
			System.out.println("async run (future) " + op.getName());
		});

	}

	static int a = 5;

	@SubCommand(inputArgs = "sleep")
	private void sleep(final CommandSender sender, final String[] args) {
		for (int i = 0; i < 50; i++) {
			FutureHandler.sync.run(() -> {
				try {
					System.out.println("Current TPS: " + Common.getTPSCounter().getTPS() + ". " +
									   "Sleeping for " + (a++) + "ms on tick " + Common.getTPSCounter().getTick() + ". " +
									   "Allocate = " + ReflectUtil.getFieldValue(FutureHandler.sync.getClass(), "allocate", FutureHandler.sync));
					Thread.sleep(a);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}, null, 0);
		}
	}

	@SubCommand(inputArgs = "chunkd")
	private void chunkd(final CommandSender sender, final String[] args) {
		FutureHandler.async.run(() -> {
			for (final AtomicInteger x = new AtomicInteger(); x.get() < 100; x.getAndIncrement()) {
				for (final AtomicInteger z = new AtomicInteger(); z.get() < 100; z.getAndIncrement()) {
					for (final AtomicInteger y = new AtomicInteger(); y.get() < 100; y.getAndIncrement()) {
						FutureHandler.sync.run(() -> {
							Bukkit.getWorld("world").getBlockAt(x.get(), y.get(), z.get()).setType(Material.BLACK_CONCRETE);
						}, null, 0);
					}
				}
			}
		}, null);
	}

	static {
		Bukkit.getWorlds().get(0).getBlockAt(0, 0, 0).setType(XMaterial.ACACIA_SIGN.toMaterial());
		Common.runLater(() -> {
			final Sign sign = (Sign) Bukkit.getWorlds().get(0).getBlockAt(0, 0, 0).getState();
			sign.setEditable(true);
			sign.update(true);
		});
	}

	@SubCommand(inputArgs = "sign")
	private void sign(final CommandSender sender, final String[] args) {
		Chat.send(getPlayer(sender), "&3Send a message boi");
		PromptUtil.sign(getPlayer(sender), (Sign) Bukkit.getWorlds().get(0).getBlockAt(0, 0, 0).getState(), (p) -> {
			Chat.send(sender, "&aYou said '&r" + Arrays.toString(p.getLines()) + "&a'.");
		});
	}

}
