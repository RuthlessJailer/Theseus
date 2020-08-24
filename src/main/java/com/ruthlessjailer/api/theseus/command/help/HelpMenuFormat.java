package com.ruthlessjailer.api.theseus.command.help;

import com.ruthlessjailer.api.theseus.Chat;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class HelpMenuFormat {

	public static final HelpMenuFormat DEFAULT_FORMAT = new HelpMenuFormat(
			"&5Suggest&b: ${command}",
			"&8/&9${command.label} &e${choice}&8| &8<&3${variable.argument}&8>",
			"${page.back}&4<<${page.none}&e&l&m------&r&e[ &bHelp for &8/&9${command.label} &e]&l&m------${page.forward}&r&4>>",
			"&e&m&l-------------");

	private final String suggest;//&5Suggest&b: ${command}
	private final String command;//&8/&9${command.label} &e${choice}&8| &8<&3${variable.argument}&8>
	private final String header;//${page.back}&4<<${page.none}&e&l&m------&r&e[ &bHelp for &8/&9${command.label} &e]&l&m------${page.forward}&r&4>>
	private final String footer;//&e&m&l-------------

	public HelpMenuFormat(@NonNull final String suggest, @NonNull final String command, @NonNull final String header, @NonNull final String footer) {
		this.suggest = Chat.colorize(suggest);
		this.command = Chat.colorize(command);
		this.header  = Chat.colorize(header);
		this.footer  = Chat.colorize(footer);
	}

}
