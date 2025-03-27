package org.keithkim.typestrql.predicate;

import lombok.EqualsAndHashCode;
import org.keithkim.typestrql.expression.Expr;
import org.keithkim.typestrql.expression.UnaryPredicate;
import org.keithkim.typestrql.type.Rows;

import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class Exists<T extends Rows> extends UnaryPredicate<T> implements Predicate {
    public Exists(Expr<T> subQuery, Set<Map.Entry<String, Object>> bindEntries) {
        super("EXISTS", subQuery, bindEntries);
    }

//    public String sql() {
//        return "EXISTS " + grouped(subQuery.sql());
//    }
}
