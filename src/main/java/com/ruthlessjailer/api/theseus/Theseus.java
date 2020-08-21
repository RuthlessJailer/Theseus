package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.example.TestCommand;

public class Theseus extends PluginBase {

	@Override
	protected void onStart() {
		System.out.println("Starting.");
		this.registerCommands(new TestCommand());
	}
}
