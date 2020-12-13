package Data;

import Controller.HelperObjects.MixingProxyCapsuleDBEntry;
import Objects.CapsuleLog;
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

    public boolean containsCapsule(CapsuleLog capsuleLog) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM capsules WHERE token = ?");
        stmt.setBytes(1, capsuleLog.getToken());

        //Run query
        ResultSet rs =  stmt.executeQuery();

        return rs.next();
    }

    public void addCapsule(CapsuleLog capsuleLog) throws SQLException {
        //Extract the capsule info for the db tables
        byte[] token = capsuleLog.getToken();
        byte[] facilityKey = capsuleLog.getFacilityKey();
        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(capsuleLog.getStartTime());
        java.sql.Timestamp stopTime = java.sql.Timestamp.valueOf(capsuleLog.getStopTime());

        //Create query
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO capsules(token, facility_key, start_time, stop_time) VALUES (?, ?, ?, ?)");
        stmt.setBytes(1, token);
        stmt.setBytes(2, facilityKey);
        stmt.setTimestamp(3, startTime);
        stmt.setTimestamp(4, stopTime);

        stmt.executeUpdate();
    }

    public ArrayList<CapsuleLog> extractCapsules() throws SQLException {
        ArrayList<CapsuleLog> capsuleLogs = new ArrayList<>();

        //Extract all capsules from the db
        PreparedStatement extract = conn.prepareStatement("SELECT * FROM capsules");
        ResultSet rs = extract.executeQuery();
        while (rs.next()) {
            byte[] token = rs.getBytes("token");
            byte[] facilityKey = rs.getBytes("facility_key");
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            LocalDateTime stopTime = rs.getTimestamp("stop_time").toLocalDateTime();

            capsuleLogs.add( new CapsuleLog(token, startTime, stopTime, facilityKey));
        }

        return capsuleLogs;
    }

    public void deleteCapsules(ArrayList<CapsuleLog> capsuleLogs) throws SQLException {
        //Delete the recently fetched capsules
        PreparedStatement delete = conn.prepareStatement("DELETE FROM capsules WHERE token = ?");
        for( CapsuleLog capsuleLog : capsuleLogs) {
            delete.setBytes(1, capsuleLog.getToken());
            delete.addBatch();
        }
        delete.executeBatch();
    }

    public ArrayList<MixingProxyCapsuleDBEntry> getAllCapsules() throws SQLException {
        //Prepare query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM capsules");

        //run the query
        ResultSet rs = stmt.executeQuery();

        //Extract the db entries
        ArrayList<MixingProxyCapsuleDBEntry> dbEntries = new ArrayList<>();
        while (rs.next()) {
            byte[] token = rs.getBytes("token");
            byte[] facilityKey = rs.getBytes("facility_key");
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            LocalDateTime stopTime = rs.getTimestamp("stop_time").toLocalDateTime();

            dbEntries.add(new MixingProxyCapsuleDBEntry(token, facilityKey, startTime, stopTime));
        }

        return dbEntries;
    }
}
