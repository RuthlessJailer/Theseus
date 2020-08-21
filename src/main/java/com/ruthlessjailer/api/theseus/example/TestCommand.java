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

	@SubCommand(inputArgs = "test create %s %e", argTypes = {String.class, Material.class})
	private void create() {

	}

}
