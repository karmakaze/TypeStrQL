package org.keithkim.demo.quicklog;

import org.keithkim.safeql.schema.Entity;

import java.beans.ConstructorProperties;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Account extends Entity<Long> {
    public static class Table extends org.keithkim.safeql.schema.Table<Account> {
        public Table(String tableExpr, String alias) {
            super(Account.class, tableExpr, alias);
        }

        public class Id extends SqlColumn<Long> {
            public Id() {
                super("id");
            }
        }
        public class FullName extends SqlColumn<String> {
            public FullName() {
                super("full_name");
            }
        }

        public final Id idCol = new Id();
        public final FullName fullNameCol = new FullName();

        public Accounts all() {
            return where(null);
        }

        public Accounts where(String criteria) {
            return new Accounts(super.where(criteria));
        }
    }

    public Long id;
    public String fullName;
    public String email;
    public String planName;
    public Instant expiresAt;

    public Map<Long, Project> projects = new HashMap<>();

    @ConstructorProperties({"id", "full_name", "email", "plan_name", "expires_at"})
    public Account(long id, String fullName, String email, String planName, Instant expiresAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.planName = planName;
        this.expiresAt = expiresAt;
    }

    void addProject(Project project) {
        this.projects.put(project.id, project);
    }

    public String toString() {
        return String.format("Account<id:%d, fullName:%s, email:%s, planName:%s, expiresAt:%s, projects: %s>",
                id, fullName, email, planName, expiresAt, projects.values());
    }
}
