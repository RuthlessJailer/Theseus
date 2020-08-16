package com.ruthlessjailer.api.theseus.item;

import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

@ToString
@AllArgsConstructor
public enum XColor {


	BLACK(DyeColor.BLACK),
	DARK_BLUE(DyeColor.BLUE),
	DARK_GREEN(DyeColor.GREEN),
	DARK_AQUA(DyeColor.CYAN),
	DARK_PURPLE(DyeColor.PURPLE),
	GOLD(DyeColor.ORANGE),
	GRAY(ReflectUtil.getEnum(DyeColor.class, "LIGHT_GRAY", "SILVER"), ChatColor.GRAY, "SILVER"),
	DARK_GRAY(DyeColor.GRAY),
	BLUE(DyeColor.BLUE),
	GREEN(DyeColor.LIME),
	AQUA(DyeColor.LIGHT_BLUE),
	RED(DyeColor.RED),
	LIGHT_PURPLE(DyeColor.MAGENTA),
	YELLOW(DyeColor.YELLOW),
	WHITE(DyeColor.WHITE),

	BROWN(DyeColor.BROWN, ChatColor.GOLD),
	PINK(DyeColor.PINK, ChatColor.LIGHT_PURPLE);

	@Getter
	private final ChatColor chatColor;

	@Getter
	private final DyeColor dyeColor;

	private final String legacyName;

	XColor(final DyeColor dyeColor){
		this(dyeColor, null);
	}

	XColor(final DyeColor dyeColor, final ChatColor chatColor){
		this(dyeColor, chatColor, null);
	}

	XColor(final DyeColor dyeColor, final ChatColor chatColor, final String legacyName){
		this.dyeColor   = dyeColor;
		this.chatColor  = chatColor != null ? chatColor : ChatColor.valueOf(this.name());
		this.legacyName = legacyName == null ? "" : legacyName;
	}

	public net.md_5.bungee.api.ChatColor getBungee(){ return this.chatColor.asBungee(); }

	public static XColor fromChatColor(final ChatColor chatColor){
		for(final XColor x : XColor.values()){
			if(x.chatColor == chatColor || x.legacyName.equals(chatColor.name())){
				return x;
			}
		}
		throw new IllegalStateException("Error parsing color "+chatColor.name()+".");
	}

	public static XColor fromBungee(final net.md_5.bungee.api.ChatColor chatColor){
		for(final XColor x : XColor.values()){
			if(x.getBungee() == chatColor || x.legacyName.equals(chatColor.name())){
				return x;
			}
		}
		throw new IllegalStateException("Error parsing color "+chatColor.name()+".");
	}

	public static XColor fromDyeColor(final DyeColor dyeColor){
		for(final XColor x : XColor.values()){
			if(x.dyeColor == dyeColor || x.legacyName.equals(dyeColor.name())){
				return x;
			}
		}
		throw new IllegalStateException("Error parsing color "+dyeColor.name()+".");
	}

	public static XColor fromName(final String name){
		final String caps = name.toUpperCase();
		for(final XColor x : XColor.values()){
			if(x.name().equals(caps) || x.chatColor.name().equals(caps) || x.dyeColor.name().equals(caps) || x.legacyName
					.equals(name)){
				return x;
			}
		}
		throw new IllegalArgumentException("No XColor found for "+caps+" .");
	}

}
