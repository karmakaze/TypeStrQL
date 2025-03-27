package org.keithkim.typestrql.sample.projects;

import org.keithkim.typestrql.annotation.Column;
import org.keithkim.typestrql.annotation.Table;

import java.time.Instant;

import static org.keithkim.typestrql.annotation.ColumnOption.ALLOW_NULL;
import static org.keithkim.typestrql.annotation.ColumnOption.PRIMARY_KEY;

@Table
public abstract class ProjectTable {
    @Column(options = {PRIMARY_KEY})
    Long id;

    @Column(references = "Account")
    Long accountId;

    @Column
    String name;

    @Column(options = ALLOW_NULL)
    String domain;

    @Column
    Instant createdAt;

    @Column
    Instant updatedAt;
}
