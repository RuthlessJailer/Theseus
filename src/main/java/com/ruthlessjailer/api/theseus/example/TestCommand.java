package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.command.CommandBase;
import com.ruthlessjailer.api.theseus.command.SubCommand;
import com.ruthlessjailer.api.theseus.command.SuperiorCommand;
import org.bukkit.Material;

public class TestCommand extends CommandBase implements SuperiorCommand {

	public TestCommand() {
		super("test");
	}

	@Override
	protected void runCommand() {
		this.sender.sendMessage("yeeted");
	}

	@SubCommand(inputArgs = "create|new %s<Name> %e", argTypes = Material.class)
	private void create(final String name, final Material test) {
		this.sender.sendMessage(name + " " + test);
	}

	@SubCommand(inputArgs = "delete|remove %b %d<Number> %e", argTypes = Material.class)
	private void delete(final Boolean bool, final Double doub, final Material test) {
		this.sender.sendMessage(bool + " " + doub + " " + test);
	}

}
