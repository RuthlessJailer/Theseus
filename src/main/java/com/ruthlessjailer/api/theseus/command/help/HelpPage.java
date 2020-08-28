package com.ruthlessjailer.api.theseus.command.help;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Vadim Hagedorn
 */
@AllArgsConstructor
@Getter
public final class HelpPage {

	private final HelpLine[] lines;

}
