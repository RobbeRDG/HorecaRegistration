package Data;

import Common.Objects.Capsule;
import Common.Objects.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DBConnection {
    private static String url;
    private Connection conn;

    public DBConnection() throws IOException {
        //Get the db url
        BufferedReader reader = new BufferedReader(new FileReader(
                "Resources/private/dbLogins/mixingProxy/url.txt"));
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

    public void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO valid_tokens(token, date) VALUES (?, ?)");

        //Generate sql compatible date from date
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);

        for (byte[] token : tokens) {
            stmt.setBytes(1, token);
            stmt.setDate(2, sqlDate);
            stmt.addBatch();
        }

        stmt.executeBatch();
    }

    public ResultSet getValidToken(byte[] tokenBytes, LocalDate date) throws SQLException {
        //Generate sql compatible date from date
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);

        //Create query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM valid_tokens WHERE token = ? AND date = ?");
        stmt.setBytes(1, tokenBytes);
        stmt.setDate(2, sqlDate);

        //Run query
        return stmt.executeQuery();
    }

    public boolean containsCapsule(Capsule capsule) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM capsules WHERE token = ?");
        stmt.setBytes(1, capsule.getToken().getTokenBytes());

        //Run query
        ResultSet rs =  stmt.executeQuery();

        return rs.next();
    }

    public void addCapsule(Capsule capsule) throws SQLException {
        //Extract the capsule info for the db tables
        byte[] token = capsule.getToken().getTokenBytes();
        byte[] facilityKey = capsule.getFacilityKey();
        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(capsule.getStartTime());
        java.sql.Timestamp stopTime = java.sql.Timestamp.valueOf(capsule.getStopTime());
        java.sql.Timestamp receivedTime = java.sql.Timestamp.valueOf(LocalDateTime.now());


        //Create query
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO capsules(token, facility_key, start_time, stop_time, received_time) VALUES (?, ?, ?, ?, ?)");
        stmt.setBytes(1, token);
        stmt.setBytes(2, facilityKey);
        stmt.setTimestamp(3, startTime);
        stmt.setTimestamp(4, stopTime);
        stmt.setTimestamp(5, receivedTime);

        stmt.executeUpdate();
    }
}
