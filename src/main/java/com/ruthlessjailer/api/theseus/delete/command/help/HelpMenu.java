package com.ruthlessjailer.api.theseus.delete.command.help;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author RuthlessJailer
 */
@AllArgsConstructor
@Getter
public final class HelpMenu {

	private final HelpPage[] pages;
	private final int        pageSize;
	private final int        pageCount;

}
