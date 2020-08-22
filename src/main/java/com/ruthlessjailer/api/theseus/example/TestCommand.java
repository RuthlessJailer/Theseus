package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.command.CommandBase;
import com.ruthlessjailer.api.theseus.command.SubCommand;
import com.ruthlessjailer.api.theseus.command.SuperiorCommand;
import org.bukkit.Material;

public class TestCommand extends CommandBase implements SuperiorCommand {

	public TestCommand() {
		super("test|yeat");
	}

	@Override
	protected void runCommand() {
		this.sender.sendMessage("yeeted");
	}

	@SubCommand(inputArgs = "test create|new %s %e", argTypes = Material.class)
	private void create(final String name, final Material test) {

	}

}
