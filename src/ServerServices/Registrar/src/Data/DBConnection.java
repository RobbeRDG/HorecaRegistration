package Data;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    public boolean containsCateringFacility(int facilityIdentifier) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cateringfacilities WHERE catering_identifier = ?");
        stmt.setInt(1, facilityIdentifier);

        //Run query
        ResultSet response = stmt.executeQuery();

        if (response == null) return false;
        else return true;
    }

    public void registerCateringFacility(int facilityIdentifier) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO cateringfacilities(catering_identifier) VALUES (?)");
        stmt.setInt(1, facilityIdentifier);

        stmt.executeUpdate();
    }

    public void addPseudonyms(int facilityIdentifier, HashMap<Calendar,byte[]> facilityPseudonyms) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO pseudonyms(catering_identifier, date, pseudonym) VALUES (?, ?, ?)");

        for (Map.Entry<Calendar, byte[]> entry : facilityPseudonyms.entrySet()) {
            Calendar day = entry.getKey();
            byte[] pseudonym = entry.getValue();

            //Generate sql compatible date from date
            java.sql.Date sqlDate = new java.sql.Date(day.getTime().getTime());

            stmt.setInt(1, facilityIdentifier);
            stmt.setDate(2, sqlDate);
            stmt.setBytes(3, pseudonym);
            stmt.addBatch();
        }

        stmt.executeBatch();
    }


    ///////////////////////////////////////////////////////////////////
    ///         USER ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    public boolean containsUser(int userIdentifier) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_identifier = ?");
        stmt.setInt(1, userIdentifier);

        //Run query
        ResultSet response = stmt.executeQuery();

        if (response == null) return false;
        else return true;
    }

    public void registerUser(int userIdentifier) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users(user_identifier) VALUES (?)");
        stmt.setInt(1, userIdentifier);

        stmt.executeUpdate();
    }
}
