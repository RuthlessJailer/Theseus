package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.command.CommandBase;
import com.ruthlessjailer.api.theseus.command.SubCommand;
import com.ruthlessjailer.api.theseus.command.SuperiorCommand;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import com.ruthlessjailer.api.theseus.multiversion.XColor;
import lombok.NonNull;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;

/**
 * @author RuthlessJailer
 */
public class TestCommand extends CommandBase implements SuperiorCommand {

	static MenuBase menuBase = new TestMenu();

	public TestCommand() {
		super("test");
	}

	@Override
	protected synchronized void runCommand(@NonNull final CommandSender sender, final String[] args, @NonNull final String label) {
	}

	@SubCommand(inputArgs = "color %i")
	private synchronized void color(final Integer color) {
		final XColor converted = XColor.fromColor(new Color(color));
		Chat.send(this.sender, converted + converted.name());
	}

	@SubCommand(inputArgs = "menu")
	private synchronized void menu() {
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
		menuBase.displayTo(getPlayer(this.sender));
	}

	private Player getPlayer(@NonNull final CommandSender sender) {
		if (!(sender instanceof Player)) {
			Chat.send(sender, "yayeet players only son youre dumb");
			throw new CommandException();
		}
		return (Player) sender;
	}

}
