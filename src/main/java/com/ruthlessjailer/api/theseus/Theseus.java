package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.example.TestCommand;
import com.ruthlessjailer.api.theseus.example.TestListener;
import com.ruthlessjailer.api.theseus.menu.MenuBase;

/**
 * For testing purposes only.<p>
 * Will be removed in releases.
 *
 * @author Vadim Hagedorn
 */
public final class Theseus extends PluginBase {

	static {
		Chat.setDebugMode(true);
	}

	@Override
	protected void onStart() {
		System.out.println("Starting.");

		this.registerCommands(new TestCommand());
		this.registerEvents(new TestListener());
		new MenuBase() {};
	}
}
