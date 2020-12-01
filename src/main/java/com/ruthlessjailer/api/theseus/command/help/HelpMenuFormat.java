package com.ruthlessjailer.api.theseus.command.help;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * See wiki for full documentation: https://github.com/RuthlessJailer/Theseus
 * <p>
 * Format object to override color scheme/style of automatically generated help menu.
 * <p>
 * See {@link HelpMenuFormat#DEFAULT_FORMAT} for a shallow example of usage.
 *
 * @author RuthlessJailer
 */
@Getter
@AllArgsConstructor
public final class HelpMenuFormat {

	public static final HelpMenuFormat DEFAULT_FORMAT = new HelpMenuFormat(
			"&5Suggest&b: " + Placeholder.COMMAND,
			"&8/&9" + Placeholder.COMMAND,
			"&8<&3" + Placeholder.VARIABLE + "&8>",
			"&e" + Placeholder.CHOICE,
			"&8|",
			Placeholder.PREVIOUS + "&e&l&m------&r&e[ &bHelp for &8/&9" + Placeholder.COMMAND + " &8(&a" + Placeholder.PAGE + "&8) &e]&l&m------" + Placeholder.NEXT,
			"&4<<",
			"&4>>",
			"&e&m&l--------------------------------------",
			5);

	private final String suggest;
	private final String command;
	private final String variable;
	private final String choice;
	private final String separator;
	private final String header;
	private final String previous;
	private final String next;
	private final String footer;//will be multiplied out to the length of page size
	private final int    pageSize;

	public static final class Placeholder {
		public static final String COMMAND  = "${command}";
		public static final String VARIABLE = "${variable}";
		public static final String CHOICE   = "${choice}";
		public static final String PREVIOUS = "${previous}";
		public static final String NEXT     = "${next}";
		public static final String PAGE     = "${page}";

	}

}
