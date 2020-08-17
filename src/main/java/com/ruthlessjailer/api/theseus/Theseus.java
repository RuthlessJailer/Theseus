package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.menu.TestListener;

public final class Theseus extends PluginBase {

	@Override
	protected void onStart() {
		this.getServer().getPluginManager().registerEvents(new TestListener(), this);
	}


}
