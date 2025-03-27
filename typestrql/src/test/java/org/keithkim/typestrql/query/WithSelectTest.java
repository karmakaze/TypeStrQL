package org.keithkim.typestrql.query;

import com.google.common.base.Joiner;
import org.junit.jupiter.api.Test;
import org.keithkim.typestrql.schema.Entity;
import org.keithkim.typestrql.schema.Table;
import org.keithkim.typestrql.test.Account0;
import org.keithkim.typestrql.test.Project0;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class WithSelectTest {
    @Test
    void withZeroCommon_sql_justHasTheSelect() {
        With<Account0> withAccount = new With<>();
        WithSelect<Account0> select = new WithSelect<>(withAccount, new Table<>(Account0.class, "account"));

        assertEquals("SELECT * FROM account", select.sql());
    }

    @Test
    void withOneCommon_sql_hasSingleWithAndSelect() {
        Table<Account0> accountTable = new Table<>(Account0.class, "SELECT * FROM account WHERE MOD(id, 3) = 1", "a");
        With<Account0> withAccount = new With<>(accountTable);
        WithSelect<Account0> select = new WithSelect<>(withAccount, new Table<>(Account0.class, "a"));

        assertEquals("WITH a AS (SELECT * FROM account WHERE MOD(id, 3) = 1\n     )\nSELECT * FROM a", select.sql());
    }

    @Test
    void withTwoCommon_sql_hasBothWithAndSelect() {
        Table<Account0> fizzTable = new Table<>(Account0.class, "SELECT * FROM account WHERE MOD(id, 3) = 0", "fizz");
        Table<Project0> buzzTable = new Table<>(Project0.class, "SELECT * FROM project WHERE MOD(id, 5) = 0", "buzz");
        With<Account0> withAccount = new With<>(fizzTable, buzzTable);

        Account0.Table accountTable = new Account0.Table("fizz", "f");
        Project0.Table projectTable = new Project0.Table("buzz", "b");
        Join joinQuery = new Join(accountTable, accountTable.idCol, projectTable, projectTable.accountIdCol);;
        Table fizzAndBuzz = new Table(Account0.class, joinQuery.sql());

        WithSelect<Account0> select = new WithSelect<>(withAccount, fizzAndBuzz);

        String expected = Joiner.on("\n").join(asList(
                "WITH fizz AS (SELECT * FROM account WHERE MOD(id, 3) = 0",
                "     ),",
                "     buzz AS (SELECT * FROM project WHERE MOD(id, 5) = 0",
                "     )",
                "SELECT * FROM (fizz f JOIN buzz b ON f.id = b.account_id)"));
        assertEquals(expected, select.sql());
    }
}
