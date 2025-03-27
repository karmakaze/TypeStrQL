package org.keithkim.typestrql.expression;

import java.util.Map;
import java.util.Set;

public interface Sql<S> {
    String sql();
    boolean isTerm();
    Set<Map.Entry<String, Object>> allBindEntries();
}
