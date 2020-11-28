package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.command.CommandBase;
import com.ruthlessjailer.api.theseus.command.SubCommand;
import com.ruthlessjailer.api.theseus.command.SuperiorCommand;
import com.ruthlessjailer.api.theseus.menu.ButtonAction;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapterRegistry;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;

/**
 * @author RuthlessJailer
 */
public class TestCommand extends CommandBase implements SuperiorCommand {

	public TestCommand() {
		super("test");
	}

	@Override
	protected synchronized void runCommand(@NonNull final CommandSender sender, final String[] args, @NonNull final String label) {
		if (sender instanceof Player) {
			((Player) sender).setVelocity(((Player) sender).getVelocity().setY(15));
		}

		if (args.length >= 1) {
			sender.sendMessage(TypeAdapterRegistry.get(Integer.class).convert(args[0]) + ", integer");
			sender.sendMessage(TypeAdapterRegistry.get(int.class).convert(args[0]) + ", int");
		}

		if (args.length >= 2) {
			sender.sendMessage(TypeAdapterRegistry.get(Double.class).convert(args[1]) + ", double");
		}

		sender.sendMessage("YEET");
	}

	@SubCommand(inputArgs = "test %p")
	private synchronized void test(final OfflinePlayer player) {
		this.sender.sendMessage("test " + player.getName());
	}

	@SubCommand(inputArgs = "create|new %s<Name> %e", argTypes = Material.class)
	private synchronized void create(final String name, final Material test) {
		this.sender.sendMessage(name + " " + test);
	}

	@SubCommand(inputArgs = "delete|remove %b %d<Number> %e", argTypes = Material.class)
	private synchronized void delete(final Boolean bool, final Double doub, final Material test) {
		this.sender.sendMessage(bool + " " + doub + " " + test);
	}

	@SubCommand(inputArgs = "action add|new|create %s<Name>")
	public synchronized void actionAdd(final String name) {
		this.sender.sendMessage("created: " + name);
	}

	@SubCommand(inputArgs = "action delete|remove %s<Name>")
	public synchronized void actionDelete(final String name) {
		this.sender.sendMessage("deleted: " + name);
	}

	@SubCommand(inputArgs = "list")
	private synchronized void list() {
		this.sender.sendMessage("list: (nothing here)");
	}

	@SubCommand(inputArgs = "menu")
	private synchronized void menu() {

		System.out.println("yeeting:");
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final ObjectOutputStream    oos  = new ObjectOutputStream(baos);
			oos.writeObject((ButtonAction & Serializable) (event, clicker, click) -> {
				System.out.println("yEEEEEEEEEEEEEEEEEEEEEE");
			});

			final byte[] bytes = baos.toByteArray();

			System.out.println(new String(bytes));
			oos.close();
			baos.close();

			final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			final ObjectInputStream    ois  = new ObjectInputStream(bais);

			final ButtonAction deserialized = (ButtonAction) ois.readObject();
			deserialized.onClick(null, null, null);
		} catch (final Exception e) {
			System.err.println("motherfucker threw an exception");
			e.printStackTrace();
		}
		System.out.println("yeeted");

		final MenuBase menu = new TestMenu();

		menu.displayTo(getPlayer(this.sender));
	}

	private Player getPlayer(@NonNull final CommandSender sender) {
		if (!(sender instanceof Player)) {
			Chat.send(sender, "yayeet players only son youre dumb");
			throw new CommandException();
		}
		return (Player) sender;
	}

}
