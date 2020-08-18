package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.example.TestCommand;
import com.ruthlessjailer.api.theseus.example.TestListener;

public final class Theseus extends PluginBase {

	static {
		Chat.setDebugMode(true);
		System.out.println("debug mode");
		Chat.info("TESTING");
	}

	@Override
	protected void onStart() {
		this.registerEvents(new TestListener());
		this.registerCommands(new TestCommand());
		System.out.println("ok");
	}


}
