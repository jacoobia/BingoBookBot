package com.jacoobia.bingobookbot.utils;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.Locale.ROOT;

/**
 * A universal portable class that I use to manage strings in different ways,
 * updated to include some blank checks.
 */
@SuppressWarnings("unused")
public class StringUtils {

    public static String BLANK = "";
    public static String SPACE = " ";
    public static String NEW_LINE = "\n";

    /**
     * The all-in-one solution to ensure that a string
     * has some kind of char value to it
     * @param string the string to check
     * @return boolean is the string empty, blank or null
     */
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty() || isBlank(string);
    }

    /**
     * Checks if a string is filled with blank spaces
     * rather than just an empty string
     * @param string the string to check
     * @return boolean is it blank or not
     */
    public static boolean isBlank(String string) {
        String[] arr = string.split("(?!^)");
        int length = string.length();
        int blanks = (int) Arrays.stream(arr).filter(" "::equals).count();
        return blanks == length;
    }

    /**
     * Prints a message to the console without colour control
     * @param message the message to print with a timestamp
     */
    public static void print(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String prefix = sdf.format(new Date());
        System.out.println("[" + prefix + "]: " + message);
    }

    /**
     * Takes in a bunch of messages and then prints them
     * one by one to the console.
     * @param messages the messages you wanna print
     */
    public static void printMultiLine(String... messages) {
        Arrays.stream(messages).forEach(StringUtils::print);
    }

    /**
     * Converts string input into int amount to be parsed
     * Example usage would be withdrawing 100k of an item
     * from your bank. Converts to 100000 then you can parse
     * it safely.
     * @param s string input
     * @return our converted string
     */
    public static String replaceNumberShortcuts(String s) {
        final String B = "000000000";
        final String M = "000000";
        final String K = "000";

        String output = s;
        if (s.contains("max")) output = output.replaceAll("max", String.valueOf(Integer.MAX_VALUE));
        if (s.contains("b")) output = output.replaceAll("b", B);
        if (s.contains("m")) output = output.replaceAll("m", M);
        if (s.contains("k")) output = output.replaceAll("k", K);

        return output;
    }

    /**
     * Converts a byte buffer to a string for parsing/reading
     * @param in the byte buffer input
     */
    public static synchronized String bufferToString(ByteBuffer in) {
        StringBuilder builder = new StringBuilder();
        byte date;
        while ((date = in.get()) != 10)
        {
            builder.append((char) date);
        }
        return builder.toString();
    }

    /**
     * Capitalize the first letter of each word in a string
     * @param str the string to capitalize
     */
    public static String capitalize(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (i == 0) {
                str = String.format("%s%s",
                        Character.toUpperCase(str.charAt(0)),
                        str.substring(1));
            }
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                if (i + 1 < str.length()) {
                    str = String.format("%s%s%s",
                            str.subSequence(0, i + 1),
                            Character.toUpperCase(str.charAt(i + 1)),
                            str.substring(i + 2));
                }
            }
        }
        return str;
    }

    /**
     * Parses a string to an int safely
     * @param s string input
     * @return integer output
     */
    public static int parseInt(String s) {
        int i = 0;
        try {
            String parse = replaceNumberShortcuts(s);
            i = Integer.parseInt(parse);
        } catch (Exception e) {
            print("Input was not strictly an int string.");
        }
        return i;
    }

    /**
     * Converts string list to a single string with spacing.
     * @param in list of strings
     * @return a built string
     */
    public static String listToString(List<String> in) {
        StringBuilder out = new StringBuilder();
        for (String s : in) {
            out.append(s);
            if (in.indexOf(s) != (in.size() - 1))
                out.append(" ");
        }
        return out.toString();
    }

    /**
     * Attempts to parse a string to a double to check if it's numeric,
     * if it fails and throws then it's obviously not numeric thus returns
     * false
     * @param str the string to check
     * @return bool is it numberic
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if any strings match a certain string ignoring case
     * @param value the string to match
     * @param any the variable length list of strings to check
     * @return bool did any of them match the value
     */
    public static boolean anyEqualsIgnoreCase(String value, String... any) {
        return Arrays.stream(any).anyMatch(other -> other.equalsIgnoreCase(value));
    }

    public static String stringArrayToString(String[] args, int startIndex) {
        StringBuilder builder = new StringBuilder();
        for(int i = startIndex; i < args.length; i++) {
            builder.append(args[i]);
            if(i < args.length - 1)
                builder.append(StringUtils.SPACE);
        }
        return builder.toString();
    }

    public static String capitalizeFirst(String text) {
        return text.substring(0, 1).toUpperCase(ROOT) + text.substring(1).toLowerCase(ROOT);
    }

    public static String convertShorthand(Integer count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        DecimalFormat format = new DecimalFormat("0.###");
        String value = format.format(count / Math.pow(1000, exp));
        return String.format("%s%c", value, "kmbt".charAt(exp - 1));
    }

}