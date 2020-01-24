package com.ruthlessjailer.util.theseus.chat;

import com.ruthlessjailer.util.theseus.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Chat {

    public static void tell(CommandSender receiver, String message){
        receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void tell(String message, CommandSender... receivers){
        for(CommandSender receiver : receivers) {
            receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void tell(CommandSender receiver, String... messages){
        for(String message : messages) {
            receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void console(String message){
        System.out.println(ConsoleColor.translateAlternateColorCodes('&', message)+ConsoleColor.RESET);
    }

    public static void console(String... messages){
        for(String message : messages) {
            System.out.println(ConsoleColor.translateAlternateColorCodes('&', message)+ConsoleColor.RESET);
        }
    }

    public static void logConsole(String... messages){
        for(String message : messages) {
            Main.log.info(ConsoleColor.translateAlternateColorCodes('&', message)+ConsoleColor.RESET);
        }
    }
    public static void logConsole(String message){
        Main.log.info(ConsoleColor.translateAlternateColorCodes('&', message)+ConsoleColor.RESET);
    }

}
