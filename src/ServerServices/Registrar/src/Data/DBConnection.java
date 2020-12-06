package Data;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class DBConnection {
    private static final String url =
            "postgres://sdqxsgwfwqrtcu:e3d76f02eceab1c64d970f8e5ade2317c2d68d7f318bad82d94da69a82361de9@ec2-46-137-124-19.eu-west-1.compute.amazonaws.com:5432/df61qauedb1vm9";
    private Connection conn;

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



    ///////////////////////////////////////////////////////////////////
    ///         CATERING FACILITY ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    public boolean containsCateringFacility(String facilityIdentifier) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cateringfacilities WHERE catering_identifier = ?");
        stmt.setString(1, facilityIdentifier);

        //Run query
        ResultSet response = stmt.executeQuery();

        if (response == null) return false;
        else return true;
    }

    public void registerCateringFacility(String facilityIdentifier) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO cateringfacilities(catering_identifier) VALUES (?)");
        stmt.setString(1, facilityIdentifier);

        stmt.executeUpdate();
    }


    ///////////////////////////////////////////////////////////////////
    ///         USER ENROLLMENT
    ///////////////////////////////////////////////////////////////////
}
