package Data;

import Common.Objects.Token;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.LocalDate;
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
    public boolean containsCateringFacility(String facilityIdentifier) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cateringfacilities WHERE facility_identifier = ?");
        stmt.setString(1, facilityIdentifier);

        //Run query
        ResultSet response = stmt.executeQuery();

        if (!response.next()) return false;
        else return true;
    }

    public void registerCateringFacility(String facilityIdentifier) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO cateringfacilities(facility_identifier) VALUES (?)");
        stmt.setString(1, facilityIdentifier);

        stmt.executeUpdate();
    }

    public void addPseudonyms(String facilityIdentifier, HashMap<LocalDate, byte[]> facilityPseudonyms) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO pseudonyms(facility_identifier, date, pseudonym) VALUES (?, ?, ?)");

        for (Map.Entry<LocalDate, byte[]> entry : facilityPseudonyms.entrySet()) {
            LocalDate day = entry.getKey();
            byte[] pseudonym = entry.getValue();

            //Generate sql compatible date from date
            java.sql.Date sqlDate = java.sql.Date.valueOf(day);

            stmt.setString(1, facilityIdentifier);
            stmt.setDate(2, sqlDate);
            stmt.setBytes(3, pseudonym);
            stmt.addBatch();
        }

        stmt.executeBatch();
    }

    public HashMap<LocalDate, byte[]> getPseudonyms(String facilityIdentifier, int year, int monthIndex) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pseudonyms WHERE facility_identifier = ? AND EXTRACT(MONTH from date) = ? AND EXTRACT(YEAR from date) = ?");
        stmt.setString(1, facilityIdentifier);
        stmt.setInt(2, monthIndex);
        stmt.setInt(3, year);

        //Run query
        ResultSet response = stmt.executeQuery();


        //Create the pseudonym hashmap
        HashMap<LocalDate, byte[]> pseudonyms = new HashMap<>();
        while (response.next()) {
            LocalDate day = response.getDate("date").toLocalDate();
            byte[] pseudonym = response.getBytes("pseudonym");

            pseudonyms.put(day, pseudonym);
        }

        if (pseudonyms.size() == 0) throw new IllegalArgumentException("Pseudonyms not yet created");
        else return pseudonyms;
    }


    ///////////////////////////////////////////////////////////////////
    ///         USER ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    public boolean containsUser(String userIdentifier) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_identifier = ?");
        stmt.setString(1, userIdentifier);

        //Run query
        ResultSet response = stmt.executeQuery();

        if (!response.next()) return false;
        else return true;
    }

    public void registerUser(String userIdentifier) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users(user_identifier) VALUES (?)");
        stmt.setString(1, userIdentifier);

        stmt.executeUpdate();
    }

    public void addTokens(String userIdentifier, LocalDate day, ArrayList<byte[]> tokens) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO tokens(user_identifier, token, date) VALUES (?, ?, ?)");

        //Generate sql compatible date from date
        java.sql.Date sqlDate = java.sql.Date.valueOf(day);

        for (byte[] token : tokens) {
            stmt.setString(1, userIdentifier);
            stmt.setBytes(2, token);
            stmt.setDate(3, sqlDate);
            stmt.addBatch();
        }

        stmt.executeBatch();
    }


    public ArrayList<byte[]> getTokens(String userIdentifier, LocalDate date) throws SQLException {
        //Create query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tokens WHERE user_identifier = ? AND date = ?");
        stmt.setString(1, userIdentifier);
        stmt.setDate(2, Date.valueOf(date));

        //Run query
        ResultSet response = stmt.executeQuery();


        //Create the pseudonym hashmap
        ArrayList<byte[]> tokens = new ArrayList<>();
        while (response.next()) {
            byte[] token = response.getBytes("token");
            tokens.add(token);
        }

        if (tokens.size() == 0) throw new IllegalArgumentException("Pseudonyms not yet created");
        else return tokens;
    }
}