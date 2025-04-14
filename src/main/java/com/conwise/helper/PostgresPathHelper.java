package com.conwise.helper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
public class PostgresPathHelper {
    public static String formatPath(List<String> pathList) {
        return "{" + pathList.stream()
                             .map(key -> "\"" + key + "\"")
                             .collect(Collectors.joining(",")) + "}";
    }
}