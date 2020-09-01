package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.command.CommandBase;
import com.ruthlessjailer.api.theseus.command.SubCommand;
import com.ruthlessjailer.api.theseus.command.SuperiorCommand;
import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapterRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author Vadim Hagedorn
 */
public class TestCommand extends CommandBase implements SuperiorCommand {

	public TestCommand() {
		super("test");
	}

	@Override
	protected void runCommand() {
		if (this.sender instanceof Player) {
			((Player) this.sender).setVelocity(((Player) this.sender).getVelocity().setY(15));
		}

		if (this.args.length >= 1) {
			this.sender.sendMessage(TypeAdapterRegistry.get(Integer.class).convert(this.args[0]) + ", integer");
			this.sender.sendMessage(TypeAdapterRegistry.get(int.class).convert(this.args[0]) + ", int");
		}

		if (this.args.length >= 2) {
			this.sender.sendMessage(TypeAdapterRegistry.get(Double.class).convert(this.args[1]) + ", double");
		}

		this.sender.sendMessage("YEET");
	}

	@SubCommand(inputArgs = "test %p", argTypes = {})
	private void test(final Player player) {
		this.sender.sendMessage("test " + player.getName());
	}

	@SubCommand(inputArgs = "create|new %s<Name> %e", argTypes = Material.class)
	private void create(final String name, final Material test) {
		this.sender.sendMessage(name + " " + test);
	}

	@SubCommand(inputArgs = "delete|remove %b %d<Number> %e", argTypes = Material.class)
	private void delete(final Boolean bool, final Double doub, final Material test) {
		this.sender.sendMessage(bool + " " + doub + " " + test);
	}

	@SubCommand(inputArgs = "action add|new|create %s<Name>", argTypes = {})
	public void actionAdd(final String name) {
		this.sender.sendMessage("created: " + name);
	}

	@SubCommand(inputArgs = "action delete|remove %s<Name>", argTypes = {})
	public void actionDelete(final String name) {
		this.sender.sendMessage("deleted: " + name);
	}

	@SubCommand(inputArgs = "list", argTypes = {})
	private void list() {
		this.sender.sendMessage("list: (nothing here)");
	}

}
