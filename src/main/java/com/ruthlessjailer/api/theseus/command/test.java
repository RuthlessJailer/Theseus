package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.example.TestCommand;

public final class test {

	public static void main(final String... args) {
		SubCommandManager.register(new TestCommand());
	}
}
