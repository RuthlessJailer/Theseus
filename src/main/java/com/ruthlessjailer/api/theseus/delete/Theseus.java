//package com.ruthlessjailer.api.theseus;
//
//import com.ruthlessjailer.api.theseus.delete.example.TestCommand;
//import com.ruthlessjailer.api.theseus.delete.example.TestListener;
//
///**
// * For testing purposes only.<p>
// * Will be removed in releases.
// *
// * @author RuthlessJailer
// */
//
//public final class Theseus extends PluginBase {
//
//	static {
//		Chat.setDebugMode(true);
//	}
//
//	@Override
//	protected void onStart() {
//		registerCommands(new TestCommand());
//		registerEvents(new TestListener());
//
//		final TPSCounter c = new TPSCounter();
//		System.out.println(c.getTPS());
//	}
//}
