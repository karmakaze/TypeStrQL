package org.keithkim.typestrql.sample.projects;

import org.keithkim.typestrql.annotation.Column;
import org.keithkim.typestrql.annotation.Table;

import java.time.Instant;

import static org.keithkim.typestrql.annotation.ColumnOption.PRIMARY_KEY;

@Table
public abstract class AccountTable {
    @Column(name = "id", options = {PRIMARY_KEY})
    Long id;

    @Column
    String fullName;

    @Column
    String email;

    @Column
    String planName;

    @Column
    Instant expiresAt;

    @Column
    Instant createdAt;

    @Column
    Instant updatedAt;
}
