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
 * @author Vadim Hagedorn
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
			"&e&m&l-------------",
			5);

	private final String suggest;//&5Suggest&b: ${command}
	private final String command;//&8/&9${command}
	private final String variable;//&8<&3${variable}&8>
	private final String choice;//&e${choice}
	private final String separator;//&8|
	private final String header;//${previous}&e&l&m------&r&e[ &bHelp for &8/&9${command.label} &e]&l&m------&r${next}
	private final String previous;//&4<<
	private final String next;//&4>>
	private final String footer;//&e&m&l-------------
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
