package com.ruthlessjailer.api.theseus.menu;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

/**
 * @author RuthlessJailer
 */
public class ButtonActionAdapter implements InstanceCreator<ButtonAction> {
	@Override
	public ButtonAction createInstance(final Type type) {
		return (event, clicker, click) -> {};
	}
}
