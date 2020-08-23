package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.example.TestCommand;

public final class Theseus extends PluginBase {

	static {
		Chat.setDebugMode(true);
	}

	@Override
	protected void onStart() {
		System.out.println("Starting.");
		this.registerCommands(new TestCommand());
	}
}
