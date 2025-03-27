package org.keithkim.typestrql.query;

import org.junit.jupiter.api.Test;
import org.keithkim.typestrql.schema.Table;
import org.keithkim.typestrql.test.Project0;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RawSelectTest {
    @Test
    public void resolveSimpleReturnsStringAsExpr() {
        Table<Project0> sqlSelect = new Table<>(Project0.class, "SELECT id, name FROM project");
        assertEquals("SELECT id, name FROM project", sqlSelect.sql());
    }
}
