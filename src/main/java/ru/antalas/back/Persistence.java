package ru.antalas.back;

import com.typesafe.config.Config;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import static java.sql.DriverManager.getDriver;

public class Persistence {
    public static Connection initBack(Config config) throws SQLException {
        Driver driver = getDriver(config.getString("db.url"));

        Connection conn = driver.connect(config.getString("db.url"), new Properties());
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS ACCOUNTS(ID INT PRIMARY KEY, AMOUNT NUMBER(10,2))");
        conn.createStatement().execute("INSERT INTO ACCOUNTS VALUES (1, 100)");
        conn.createStatement().execute("INSERT INTO ACCOUNTS VALUES (2, 0)");
        return conn;
    }
}
