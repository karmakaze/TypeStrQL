package org.keithkim.typestrql.query;

import com.google.common.base.Joiner;
import lombok.EqualsAndHashCode;
import org.keithkim.typestrql.expression.Expr;
import org.keithkim.typestrql.schema.Entity;
import org.keithkim.typestrql.schema.Sys;
import org.keithkim.typestrql.schema.Table;
import org.keithkim.typestrql.type.Rows;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@EqualsAndHashCode(callSuper = false)
public class Select<E extends Entity> extends Expr<Rows<E>> {
    private final Table table;
    private final List<Table<E>.SqlColumn<?>> columns;

    public Select(Table<E> table, Table<E>.SqlColumn<?>... columns) {
        this(table, asList(columns));
    }

    public Select(Table<E> table, List<Table<E>.SqlColumn<?>> columns) {
        super(null);
        this.table = table;
        this.columns = columns;
    }

    public <E extends Entity> Table<E> asTableExpr() {
        return asTableExpr(null);
    }
    public <E extends Entity> Table<E> asTableExpr(String alias) {
        return new Table<E>(table.entityClass, sql(), alias);
    }

    public String sql() {
        Map<String, ?> binds = localBinds();
        List<String> cols;
        if (columns.isEmpty()) {
            String aliasStar = table.alias().isPresent() ? table.alias().get() + ".*" : "*";
            cols = singletonList(aliasStar);
        } else {
            cols = columns.stream().map(c -> c.selectTerm(binds)).collect(toList());
        }
        String fromTable = "";
        if (table != Sys.Table.none) {
            fromTable = " FROM " + table.sqlTerm();
        }
        return String.format("SELECT %s" + fromTable, Joiner.on(", ").join(cols));
    }
}
