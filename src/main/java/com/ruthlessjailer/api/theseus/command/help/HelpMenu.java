package com.ruthlessjailer.api.theseus.command.help;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class HelpMenu {

	private final HelpPage[] pages;
	private final int        pageSize;
	private final int        pageCount;

}
