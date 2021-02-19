package com.company;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Utils {
    public static boolean isValidIpAddr(String ipAddr) {
        String[] frags = ipAddr.split("\\.");
        return frags.length == 4 && Arrays.stream(frags).allMatch(Utils::isInt);
    }

    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException n) {
            return false;
        }
    }
}
