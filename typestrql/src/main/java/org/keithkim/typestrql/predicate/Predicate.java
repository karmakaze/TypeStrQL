package org.keithkim.typestrql.predicate;

import org.keithkim.typestrql.expression.Eval;
import org.keithkim.typestrql.expression.SqlScalar;

public interface Predicate extends Eval, SqlScalar<Boolean> {
    default boolean isKnownFalse() {
        return "FALSE".equalsIgnoreCase(sql()) || Boolean.FALSE == eval();
    }

    default boolean isKnownTrue() {
        return "TRUE".equalsIgnoreCase(sql()) || Boolean.TRUE == eval();
    }
}
