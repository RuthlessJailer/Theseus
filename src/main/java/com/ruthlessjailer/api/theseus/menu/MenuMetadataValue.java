package com.ruthlessjailer.api.theseus.menu;

import com.ruthlessjailer.api.theseus.Checks;
import lombok.NonNull;
import org.bukkit.metadata.MetadataValueAdapter;

/**
 * @author RuthlessJailer
 */
public class MenuMetadataValue extends MetadataValueAdapter {
	private MenuBase menu;

	protected MenuMetadataValue(@NonNull final MenuBase menu) {
		super(Checks.instanceCheck());
		this.menu = menu;
	}

	@Override
	public MenuBase value() {
		return this.menu;
	}

	@Override
	public void invalidate() {
		this.menu = null;
	}
}
