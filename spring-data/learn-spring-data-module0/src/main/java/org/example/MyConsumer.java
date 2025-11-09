package org.example;

import java.sql.SQLException;

@FunctionalInterface
public interface MyConsumer <T> {
    void accept(T t) throws SQLException;
}
