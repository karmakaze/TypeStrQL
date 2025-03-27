package org.keithkim.typestrql.predicate;

import lombok.EqualsAndHashCode;
import org.keithkim.typestrql.expression.Expr;
import org.keithkim.typestrql.expression.TernaryExpr;

@EqualsAndHashCode(callSuper = false)
public class Between<T> extends TernaryExpr<Boolean, T, T, T> implements Predicate {
    public Between(Expr<T> subject, Expr<T> rangeMin, Expr<T> rangeMax) {
        super(subject, "BETWEEN", rangeMin, "AND", rangeMax);
    }
}
