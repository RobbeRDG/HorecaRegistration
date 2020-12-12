package Data;

import Common.Objects.CapsuleLog;
import Common.Objects.FacilityVisitLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

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

    public void markCriticalCapsules(ArrayList<FacilityVisitLog> infectedFacilities) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE capsules SET critical = true WHERE facility_key = ? AND start_time >= ? AND stop_time <= ?");


        for (FacilityVisitLog facilityVisitLog: infectedFacilities) {
            stmt.setBytes(1, facilityVisitLog.getVisitedFacility().getFacilityKey());
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(facilityVisitLog.getEntryTime()));
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(facilityVisitLog.getLeaveTime()));
            stmt.addBatch();
        }

        stmt.executeBatch();
    }

    public void markInformed(ArrayList<byte[]> acknowledgementTokens) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE capsules SET informed = true WHERE critical = true AND token = ?");


        for (byte[] acknowledgementToken: acknowledgementTokens) {
            stmt.setBytes(1, acknowledgementToken);
            stmt.addBatch();
        }

        stmt.executeBatch();
    }

    public ArrayList<byte[]> getUnacknowledgedTokens(LocalDate revealDate) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM capsules WHERE critical = true AND informed = false AND DATE(start_time) <= ?");
        stmt.setDate(1, Date.valueOf(revealDate));

        //Run query
        ResultSet rs = stmt.executeQuery();

        //Get all the uninformed tokens
        ArrayList<byte[]> uninformedTokens = new ArrayList<>();
        while (rs.next()) {
            uninformedTokens.add(rs.getBytes("token"));
        }

        return uninformedTokens;
    }

    public void addCapsules(ArrayList<CapsuleLog> capsules) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO capsules(token, facility_key, start_time, stop_time, received_time) VALUES (?, ?, ?, ?, ?)");

        for (CapsuleLog capsuleLog: capsules) {
            //Extract the capsule info for the db tables
            byte[] token = capsuleLog.getToken();
            byte[] facilityKey = capsuleLog.getFacilityKey();
            java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(capsuleLog.getStartTime());
            java.sql.Timestamp stopTime = java.sql.Timestamp.valueOf(capsuleLog.getStopTime());
            java.sql.Timestamp receivedTime = java.sql.Timestamp.valueOf(LocalDateTime.now());

            //Prepare the query
            stmt.setBytes(1, token);
            stmt.setBytes(2, facilityKey);
            stmt.setTimestamp(3, startTime);
            stmt.setTimestamp(4, stopTime);
            stmt.setTimestamp(5, receivedTime);
            stmt.addBatch();
        }

        stmt.executeBatch();
    }

    public ArrayList<CapsuleLog> getInfectedCapsules() throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM capsules WHERE critical = true");

        //Run query
        ResultSet rs = stmt.executeQuery();

        //Get all the uninformed tokens
        ArrayList<CapsuleLog> infectedCapsules = new ArrayList<>();
        while (rs.next()) {
            byte[] token = rs.getBytes("token");
            byte[] facilityKey = rs.getBytes("facility_key");
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            LocalDateTime stopTime = rs.getTimestamp("stop_time").toLocalDateTime();

            infectedCapsules.add(new CapsuleLog(token, startTime, stopTime, facilityKey));
        }

        return infectedCapsules;
    }
}
