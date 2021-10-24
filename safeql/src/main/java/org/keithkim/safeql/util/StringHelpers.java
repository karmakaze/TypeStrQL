package org.keithkim.safeql.util;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.CaseFormat.*;

public class StringHelpers {
    private static final Pattern varPattern = Pattern.compile(":[A-Za-z][A-Za-z0-9_]*");

    public static String join(String separator, Collection<?> items) {
        StringJoiner stringJoiner = new StringJoiner(separator);
        items.forEach(item -> stringJoiner.add(item.toString()));
        return stringJoiner.toString();
    }

    public static String joinMap(String separator, String keyValueSeparator, Collection<?> items) {
        StringJoiner stringJoiner = new StringJoiner(separator);
        items.forEach(item -> stringJoiner.add(item.toString()));
        return stringJoiner.toString();
    }

    public static Set<String> bindNames(String template) {
        Matcher matcher = varPattern.matcher(template);
        Set<String> names = new HashSet<>();
        while (matcher.find()) {
            String var = matcher.group();
            names.add(var.substring(1));
        }
        return names;
    }

    public static String snakeCase(String camelCase) {
        if (camelCase == null) {
            return null;
        }
        if (Character.isUpperCase(camelCase.charAt(0))) {
            return UPPER_CAMEL.to(LOWER_UNDERSCORE, camelCase);
        }
        return LOWER_CAMEL.to(LOWER_UNDERSCORE, camelCase);
    }

    public static String upperSnakeCase(String camelCase) {
        if (camelCase == null) {
            return null;
        }
        if (Character.isUpperCase(camelCase.charAt(0))) {
            return UPPER_CAMEL.to(UPPER_UNDERSCORE, camelCase);
        }
        return LOWER_CAMEL.to(UPPER_UNDERSCORE, camelCase);
    }

    public static String apply(Optional<String> input, Function<String, String> fn, String defaultValue) {
        if (input.isPresent()) {
            return fn.apply(input.get());
        }
        return defaultValue;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
