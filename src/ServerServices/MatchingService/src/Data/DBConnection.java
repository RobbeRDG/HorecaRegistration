package Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static String url;
    private Connection conn;

    public DBConnection() throws IOException {
        //Get the db url
        BufferedReader reader = new BufferedReader(new FileReader(
                "Resources/private/dbLogins/matchingService/url.txt"));
        url = reader.readLine();
    }

    public void connectToDatabase() throws SQLException, URISyntaxException {
        URI dbUri = new URI(url);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        conn = DriverManager.getConnection(dbUrl, username, password);
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }
}
