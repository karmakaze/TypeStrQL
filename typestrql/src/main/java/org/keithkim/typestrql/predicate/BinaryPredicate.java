package org.keithkim.typestrql.predicate;

import org.keithkim.typestrql.expression.BinaryExpr;
import org.keithkim.typestrql.expression.Expr;
import org.keithkim.typestrql.expression.Sql;

public class BinaryPredicate<L, R> extends BinaryExpr<Boolean, L, R> implements Predicate {
    public BinaryPredicate(Sql<L> left, String operator, Sql<R> right) {
        super(left, operator, right);
    }

    protected BinaryPredicate(String sql, Sql<L> left, String operator, Sql<R> right) {
        super(sql, left, operator, right);
    }

//    @Override
//    public Object eval() {
//        return null;
//    }
}
