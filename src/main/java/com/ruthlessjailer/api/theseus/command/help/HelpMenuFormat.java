package com.ruthlessjailer.api.theseus.command.help;

import com.ruthlessjailer.api.theseus.Chat;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class HelpMenuFormat {

	public static final HelpMenuFormat DEFAULT_FORMAT = new HelpMenuFormat(
			"&5Suggest&b: " + Placeholder.COMMAND,
			"&8/&9" + Placeholder.COMMAND,
			"&8<&3" + Placeholder.VARIABLE + "&8>",
			"&e" + Placeholder.CHOICE,
			"&8|",
			Placeholder.PREVIOUS + "&e&l&m------&r&e[ &bHelp for &8/&9" + Placeholder.COMMAND + " &e]&l&m------" + Placeholder.NEXT,
			"&4<<",
			"&4>>",
			"&e&m&l-------------",
			5);

	private final String suggest;//&5Suggest&b: ${command}
	private final String command;//&8/&9${command}
	private final String variable;//&8<&3${variable}&8>
	private final String choice;//&e${choice}&8|
	private final String separator;
	private final String header;//${previous}&e&l&m------&r&e[ &bHelp for &8/&9${command.label} &e]&l&m------&r${next}
	private final String previous;//&4<<
	private final String next;//&4>>
	private final String footer;//&e&m&l-------------
	private final int    pageSize;

	public HelpMenuFormat(@NonNull final String suggest, @NonNull final String command, @NonNull final String variable,
						  @NonNull final String choice, @NonNull final String separator, @NonNull final String header, @NonNull final String previous,
						  @NonNull final String next, @NonNull final String footer, final int pageSize) {
		this.suggest   = Chat.colorize(suggest);
		this.command   = Chat.colorize(command);
		this.variable  = Chat.colorize(variable);
		this.choice    = Chat.colorize(choice);
		this.separator = Chat.colorize(separator);
		this.header    = Chat.colorize(header);
		this.previous  = Chat.colorize(previous);
		this.next      = Chat.colorize(next);
		this.footer    = Chat.colorize(footer);
		this.pageSize  = pageSize;
	}

	public static final class Placeholder {
		public static final String COMMAND  = "${command}";
		public static final String VARIABLE = "${variable}";
		public static final String CHOICE   = "${choice}";
		public static final String PREVIOUS = "${previous}";
		public static final String NEXT     = "${next}";
	}

}
