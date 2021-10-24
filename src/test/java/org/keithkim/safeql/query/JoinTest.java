package org.keithkim.safeql.query;

import org.junit.jupiter.api.Test;
import org.keithkim.safeqldemo.projects.Account;
import org.keithkim.safeqldemo.projects.Project;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JoinTest {
    Account.Table accountTable = new Account.Table("account", "a");
    Project.Table projectTable = new Project.Table("project", "p");

    @Test
    void defaultJoin() {
        Join joinQuery = new Join(accountTable, accountTable.idCol, projectTable, projectTable.accountIdCol);
        assertEquals("account a JOIN project p ON a.id = p.account_id", joinQuery.sql());
    }

    @Test
    void defaultJoinEquate() {
        Join joinQuery = new Join(accountTable, projectTable, new Join.Equate(accountTable.idCol, projectTable.accountIdCol));
        assertEquals("account a JOIN project p ON a.id = p.account_id", joinQuery.sql());
    }

    @Test
    void fullJoinEquate() {
        Join joinQuery = new Join(Join.Type.FULL_JOIN, accountTable, projectTable, new Join.Equate(accountTable.idCol, projectTable.accountIdCol));
        assertEquals("account a FULL JOIN project p ON a.id = p.account_id", joinQuery.sql());
    }

    @Test
    void leftJoinEquate2() {
        Join joinQuery = new Join(Join.Type.LEFT_JOIN, accountTable, projectTable, new Join.Equate(accountTable.idCol, projectTable.accountIdCol), new Join.Equate(accountTable.idCol, projectTable.accountIdCol));
        assertEquals("account a LEFT JOIN project p ON (a.id = p.account_id) AND (a.id = p.account_id)", joinQuery.sql());
    }

    @Test
    void join() {
        Join joinQuery = new Join(Join.Type.JOIN, accountTable, accountTable.idCol, projectTable, projectTable.accountIdCol);
        assertEquals("account a JOIN project p ON a.id = p.account_id", joinQuery.sql());
    }

    @Test
    void leftJoin() {
        Join joinQuery = new Join(Join.Type.LEFT_JOIN, accountTable, accountTable.idCol, projectTable, projectTable.accountIdCol);
        assertEquals("account a LEFT JOIN project p ON a.id = p.account_id", joinQuery.sql());
    }

    @Test
    void rightJoin() {
        Join joinQuery = new Join(Join.Type.RIGHT_JOIN, accountTable, accountTable.idCol, projectTable, projectTable.accountIdCol);
        assertEquals("account a RIGHT JOIN project p ON a.id = p.account_id", joinQuery.sql());
    }

    @Test
    void fullJoin() {
        Join joinQuery = new Join(Join.Type.FULL_JOIN, accountTable, accountTable.idCol, projectTable, projectTable.accountIdCol);
        assertEquals("account a FULL JOIN project p ON a.id = p.account_id", joinQuery.sql());
    }

    @Test
    void crossJoin() {
        Join<Account, Project> joinQuery = new Join<>(Join.Type.CROSS_JOIN, accountTable, accountTable.idCol, projectTable, projectTable.accountIdCol);
        assertEquals("account a CROSS JOIN project p", joinQuery.sql());
    }
}
