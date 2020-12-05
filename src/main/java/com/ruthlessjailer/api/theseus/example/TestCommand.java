package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.command.CommandBase;
import com.ruthlessjailer.api.theseus.command.SubCommand;
import com.ruthlessjailer.api.theseus.command.SuperiorCommand;
import com.ruthlessjailer.api.theseus.multiversion.XColor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.awt.*;

/**
 * @author RuthlessJailer
 */
public class TestCommand extends CommandBase implements SuperiorCommand {

	static TestMenu test = new TestMenu();

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

	@SubCommand(inputArgs = "material %e", argTypes = Material.class)
	private void material(final CommandSender sender, final String[] args, final Material material) {
//		final XMaterial x = XMaterial.getXMaterial(joinArgs(1, args).replaceAll(" ", "_").toUpperCase());
//
//		Chat.send(sender, x.name());
//		Chat.send(sender, x.toItemStack().getData().toString());
//
//		if (sender instanceof Player) {
//			getPlayer(sender).getInventory().addItem(x.toItemStack());
//		}

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
	}

}
