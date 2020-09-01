package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.command.CommandBase;
import com.ruthlessjailer.api.theseus.command.SubCommand;
import com.ruthlessjailer.api.theseus.command.SuperiorCommand;
import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapterRegistry;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Vadim Hagedorn
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

	@SubCommand(inputArgs = "test %p", argTypes = {})
	private synchronized void test(final Player player) {
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

	@SubCommand(inputArgs = "action add|new|create %s<Name>", argTypes = {})
	public synchronized void actionAdd(final String name) {
		this.sender.sendMessage("created: " + name);
	}

	@SubCommand(inputArgs = "action delete|remove %s<Name>", argTypes = {})
	public synchronized void actionDelete(final String name) {
		this.sender.sendMessage("deleted: " + name);
	}

	@SubCommand(inputArgs = "list", argTypes = {})
	private synchronized void list() {
		this.sender.sendMessage("list: (nothing here)");
	}

}
