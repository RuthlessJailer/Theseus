package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.command.CommandBase;

public class TestCommand extends CommandBase {

	public TestCommand() {
		super("test|yeat");
	}

	@Override
	protected void runCommand() {
		this.sender.sendMessage("yeeted");
	}
}
