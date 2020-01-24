package com.ruthlessjailer.util.theseus.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleColor {

    private static final String CODES = "r?0426153f";

    public static final String RESET  = "\u001B[0m";//r
    public static final String YELLOW = "\u001B[7m";//?
    public static final String BLACK  = "\u001B[30m";//0
    public static final String RED    = "\u001B[31m";//4
    public static final String GREEN  = "\u001B[32m";//2
    public static final String GOLD   = "\u001B[33m";//6
    public static final String BLUE   = "\u001B[34m";//1
    public static final String PURPLE = "\u001B[35m";//5
    public static final String AQUA   = "\u001B[36m";//3
    public static final String WHITE  = "\u001B[37m";//f

    public static final List<String> COLORS = new ArrayList<>(Arrays.asList(RESET, YELLOW, BLACK, RED, GREEN, GOLD, BLUE, PURPLE, AQUA, WHITE));

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate){
        StringBuilder sb = new StringBuilder(textToTranslate);
        for(int i = 0; i < sb.length(); i++){
            if(sb.charAt(i) == altColorChar && CODES.indexOf(sb.charAt(i+1)) != -1) {
                sb.replace(i, i+2, COLORS.get(CODES.indexOf(sb.charAt(i+1))));
            }
        }
        return sb.toString();
    }

}
