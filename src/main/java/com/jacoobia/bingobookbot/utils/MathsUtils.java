package com.jacoobia.bingobookbot.utils;

public class MathsUtils {

    public static boolean perfectRoot(int x) {
        double sq = Math.sqrt(x);
        return ((sq - Math.floor(sq)) == 0);
    }

}
