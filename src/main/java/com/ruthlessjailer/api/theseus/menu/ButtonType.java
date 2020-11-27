package com.ruthlessjailer.api.theseus.menu;

import com.google.common.annotations.Beta;

/**
 * @author RuthlessJailer
 */
public enum ButtonType {

	/**
	 * No action when clicked.
	 */
	INFO,

	/**
	 * Runs code when clicked.
	 */
	ACTION,

	/**
	 * Gives up its item when clicked.
	 */
	TAKE,

	/**
	 * Receives an item when clicked.
	 * NOT IMPLEMENTED: DO NOT USE.
	 */
	@Beta
	PLACE


}
