package com.ruthlessjailer.api.theseus.menu;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

/**
 * @author RuthlessJailer
 */
public class MenuBaseAdapter implements InstanceCreator<MenuBase> {

	@Override
	public MenuBase createInstance(final Type type) {
		return new MenuBase(0, "&6Unnamed Menu") {};
	}
}
