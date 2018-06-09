package ru.antalas.persistence;

import org.h2.Driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Persistence {
    public static Connection initBack() throws SQLException {
        Driver.load();

        Connection conn = new Driver().connect("jdbc:h2:~/test", new Properties());
        conn.createStatement().execute("DROP TABLE IF EXISTS ACCOUNTS");
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS ACCOUNTS(ID INT PRIMARY KEY, AMOUNT NUMBER(10,2))");
        conn.createStatement().execute("INSERT INTO ACCOUNTS VALUES (1, 100)");
        conn.createStatement().execute("INSERT INTO ACCOUNTS VALUES (2, 0)");
        return conn;
    }
}
