package com.jacoobia.bingobookbot.utils;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class CollectionUtils {

    public static boolean isListEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isListNotEmpty(List<?> list) {
        return !isListEmpty(list);
    }

    public static <T> List<T> getListNullSafe(List<T> list) {
        return isListEmpty(list) ? Collections.emptyList() : list;
    }

    public static String joinStringList(List<String> list) {
        StringJoiner joiner = new StringJoiner(", ");
        list.forEach(joiner::add);
        return joiner.toString();
    }

}
