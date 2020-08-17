package com.ruthlessjailer.api.theseus;

public final class Theseus extends PluginBase {

	@Override
	protected void onStart() {
		this.getServer().getPluginManager().registerEvents(new TestListener(), this);
	}


}
