package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.command.SubCommand;
import com.ruthlessjailer.api.theseus.command.SuperiorCommand;

public class TestCommand /*extends CommandBase*/ implements SuperiorCommand {

	public TestCommand() {
		//super("test|yeat");
	}

	/*@Override*/
	protected void runCommand() {
		//this.sender.sendMessage("yeeted");
	}

	@SubCommand(inputArgs = "test %s", argTypes = {String.class})
	private void name() {

	}

	@SubCommand(inputArgs = "test create %s left|right", argTypes = {String.class})
	private void create() {

	}

}
