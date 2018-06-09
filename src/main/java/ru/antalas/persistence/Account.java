package ru.antalas.persistence;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;
import static java.util.Optional.of;

public class Account {
    public static Optional<BigDecimal> amount(Connection conn, String item) throws SQLException {
        PreparedStatement query = conn.prepareStatement("SELECT AMOUNT FROM ACCOUNTS WHERE ID = ?");
        query.setInt(1, parseInt(item));
        query.execute();

        ResultSet resultSet = query.getResultSet();
        return resultSet.next() ? of(resultSet.getBigDecimal("AMOUNT")) : Optional.empty();
    }

    public static void transfer(Connection conn, String src, String dst, String amt) throws SQLException {
        conn.setAutoCommit(false);
        int currentIsolation = conn.getTransactionIsolation();
        conn.setTransactionIsolation(TRANSACTION_READ_COMMITTED);

        update(conn, parseInt(src), new BigDecimal(amt).negate());
        update(conn, parseInt(dst), new BigDecimal(amt));

        conn.setTransactionIsolation(currentIsolation);
        conn.setAutoCommit(true);
    }

    private static void update(Connection conn, int accountId, BigDecimal amount) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE ACCOUNTS SET AMOUNT = AMOUNT + ? WHERE ID = ?");
        stmt.setInt(2, accountId);
        stmt.setBigDecimal(1, amount);
        stmt.executeUpdate();
    }
}
