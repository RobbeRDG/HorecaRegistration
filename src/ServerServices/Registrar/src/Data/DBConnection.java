package Data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {
    private static String url;
    private Connection conn;

    public DBConnection() throws IOException {
        //Get the db url
        BufferedReader reader = new BufferedReader(new FileReader(
                "Resources/private/dbLogins/registrar/url.txt"));
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

    public void addTokens(int userIdentifier,Calendar day, ArrayList<byte[]> tokens) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO tokens(user_identifier, token, date) VALUES (?, ?, ?)");

        //Generate sql compatible date from date
        java.sql.Date sqlDate = new java.sql.Date(day.getTime().getTime());

        for (byte[] token : tokens) {
            stmt.setInt(1, userIdentifier);
            stmt.setBytes(2, token);
            stmt.setDate(3, sqlDate);
            stmt.addBatch();
        }

        stmt.executeBatch();
    }
}
