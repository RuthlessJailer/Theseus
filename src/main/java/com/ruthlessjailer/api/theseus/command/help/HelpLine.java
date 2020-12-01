package com.ruthlessjailer.api.theseus.command.help;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * @author RuthlessJailer
 */
@AllArgsConstructor
@Getter
public final class HelpLine {

	private final String          raw;
	private final String          colorized;
	private final BaseComponent[] formatted;
	private final String[]        permissions;

}
