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
        conn.createStatement().execute("DROP TABLE IF EXISTS MYTEST");
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS MYTEST(ID INT PRIMARY KEY, NAME VARCHAR(255))");
        conn.createStatement().execute("INSERT INTO MYTEST VALUES (1, 'One')");

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(Handlers.pathTemplate()
                        .add("/item/{itemId}", in -> {
                            Map<String, Deque<String>> params = in.getQueryParameters();
                            String item = params.get("itemId").getFirst();
                            PreparedStatement query = conn.prepareStatement("SELECT NAME FROM MYTEST WHERE ID = ?");
                            query.setInt(1, parseInt(item));
                            query.execute();
                            ResultSet rs = query.getResultSet();
                            rs.next();
                            in.getResponseSender().send(rs.getString("NAME"));
                        })
                )
                .build();




        server.start();



    }
}
