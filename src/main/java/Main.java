import io.undertow.Handlers;
import io.undertow.Undertow;
import org.h2.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Deque;
import java.util.Map;
import java.util.Properties;

import static java.lang.Integer.parseInt;

public class Main {
    public static void main(String[] args) throws SQLException {
        Driver.load();

        Connection conn = new Driver().connect("jdbc:h2:~/test", new Properties());
        conn.createStatement().execute("DROP TABLE IF EXISTS ACCOUNTS");
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS ACCOUNTS(ID INT PRIMARY KEY, AMOUNT NUMBER(10,2))");
        conn.createStatement().execute("INSERT INTO ACCOUNTS VALUES (1, 100)");
        conn.createStatement().execute("INSERT INTO ACCOUNTS VALUES (2, 0)");

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(Handlers.pathTemplate()
                        .add("/account/{id}", in -> {
                            Map<String, Deque<String>> params = in.getQueryParameters();
                            String item = params.get("id").getFirst();
                            PreparedStatement query = conn.prepareStatement("SELECT AMOUNT FROM ACCOUNTS WHERE ID = ?");
                            query.setInt(1, parseInt(item));
                            query.execute();
                            ResultSet rs = query.getResultSet();
                            rs.next();
                            in.getResponseSender().send(rs.getBigDecimal("AMOUNT").toPlainString());
                        })
                        .add("/transfer/{src}/{dst}/{sum}", in -> in.setStatusCode(404))
                )
                .build();




        server.start();



    }
}
