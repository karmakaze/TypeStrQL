package org.keithkim.typestrql.schema;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Sys {
    public static class Table {
        public static org.keithkim.typestrql.schema.Table none = new org.keithkim.typestrql.schema.Table(Table.class);

        public static <E extends Entity> org.keithkim.typestrql.schema.Table<E> none() {
            return (org.keithkim.typestrql.schema.Table<E>) none;
        }

        public static <E extends Entity, T> org.keithkim.typestrql.schema.Table<E>.SqlColumn<T> column(String literal) {
            return (org.keithkim.typestrql.schema.Table<E>.SqlColumn<T>) new org.keithkim.typestrql.schema.Table(Table.class).sqlColumn(literal);
        }
    }
}
