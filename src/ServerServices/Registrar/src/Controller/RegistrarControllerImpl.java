package Controller;

import Connection.*;
import Data.DBConnection;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrarControllerImpl implements RegistarController{
    private static DBConnection dbConnection;
    private static ConnectionImpl rmiServer;
    private static SecretKey masterKey;

    public RegistrarControllerImpl() throws NoSuchAlgorithmException {
        if (dbConnection == null) dbConnection = new DBConnection();
        if (rmiServer == null) rmiServer = new ConnectionImpl(this);
        if (masterKey == null) {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // for example
            masterKey = keyGen.generateKey();
        }
    }

    public static void main(String[] args){
        try {
            RegistrarControllerImpl registrar = new RegistrarControllerImpl();
            registrar.start();
            registrar.stop();
        } catch (Exception e) {
            System.out.println("Registrar failed");
        }
    }

    private void start() {
        try {
            System.out.println("Starting Registar service...");
            //Connect to the database
            dbConnection.connectToDatabase();

            //Start the connection server
            rmiServer.startServer();

            System.out.println("Registrar service running");
        } catch (Exception e){
            System.out.println("Startup failed: " + e.getMessage());
        }

    }

    private void stop() {
        int status = 0;
        try {
            System.out.println("Stopping Registar service...");
            dbConnection.closeConnection();
        } catch (Exception e) {
            System.out.println("Shutdown failed: " + e.getMessage());
            status = -1;
        } finally {
            System.exit(status);
        }
    }

    private void handleException(Exception e) {
        e.printStackTrace();
    }



    ///////////////////////////////////////////////////////////////////
    ///         CATERING FACILITY ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    @Override
    public void registerCateringFacility(int facilityIdentifier) throws Exception {
        try {
            //Test if the identifier has the correct syntax
            if (testFacilityIdentifier(facilityIdentifier) == false) throw new IllegalArgumentException(
                    "Catering facility can't be created: The catering identifier is not the correct syntax");

            //Test if the Catering facility doesn't already exist
            if (dbConnection.containsCateringFacility(facilityIdentifier) == true) throw new IllegalArgumentException(
                    "Catering facility can't be created: the catering identifier already exists");

            //Place the new facility in the db
            dbConnection.registerCateringFacility(facilityIdentifier);

            System.out.println("New facility registered: " + facilityIdentifier);
        } catch (Exception e) {
            handleException(e);
            throw e;
        }
    }


    private boolean testFacilityIdentifier(int facilityIdentifier) {
        //Test if the identifier is 10 numbers long
        if (String.valueOf(facilityIdentifier).length() != 10) return false;

        return true;
    }

    @Override
    public HashMap<Calendar, byte[]> getPseudomyms(int facilityIdentifier, int year, int monthIndex) throws Exception {
        //test if the facility is registered
        try {
            if (!dbConnection.containsCateringFacility(facilityIdentifier)) throw new IllegalArgumentException("Couldn't create pseudonyms: Facility not registered");
        } catch (IllegalArgumentException e) {
            handleException(e);
            throw e;
        } catch (Exception e) {
            handleException(e);
            String exceptionMessage = "Couldn't create pseudonyms: something went wrong";
            throw new Exception(exceptionMessage);
        }

        //Get the days in the month
        ArrayList<Calendar> daysInMonth = generateDaysInMonth(year, monthIndex);

        //Generate new secret key for each day in month
        HashMap<Calendar,byte[]> facilitySecretKeys = generateSecretKeys(daysInMonth, facilityIdentifier);

        //Hash the secret keys to find the pseudonyms
        HashMap<Calendar,byte[]> facilityPseudonyms = generatePseudonyms(facilitySecretKeys, facilityIdentifier);

        //Place the pseudonyms in the db
        dbConnection.addPseudonyms(facilityIdentifier, facilityPseudonyms);

        return facilityPseudonyms;
    }

    private HashMap<Calendar,byte[]> generatePseudonyms(HashMap<Calendar,byte[]> facilitySecretKeys, int facilityIdentifier) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] facilityIdentifierBytes = ByteBuffer.allocate(4).putInt(facilityIdentifier).array();
        HashMap<Calendar,byte[]> pseudonyms = new HashMap<>();

        for (Map.Entry<Calendar, byte[]> entry : facilitySecretKeys.entrySet()) {
            Calendar day = entry.getKey();
            byte[] secretKey = entry.getValue();

            //Generate string from date
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmyyyy");
            String dayString = dateFormat.format(day.getTime());

            //Generate a new byte array
            byte[] temp = new byte[secretKey.length + facilityIdentifierBytes.length + dayString.getBytes().length];
            ByteBuffer buff = ByteBuffer.wrap(temp);
            buff.put(secretKey);
            buff.put(facilityIdentifierBytes);
            buff.put(dayString.getBytes());

            byte[] preHashPseudonym = buff.array();
            byte[] encodedHashPseudonym = digest.digest(preHashPseudonym);

            pseudonyms.put(day, encodedHashPseudonym);
        }

        return pseudonyms;
    }

    private HashMap<Calendar, byte[]> generateSecretKeys(ArrayList<Calendar> daysInMonth, int facilityIdentifier) {
        HashMap<Calendar,byte[]> secretKeys = new HashMap<>();

        byte[] facilityIdentifierBytes = ByteBuffer.allocate(4).putInt(facilityIdentifier).array();
        for (Calendar day : daysInMonth) {
            //Generate string from date
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmyyyy");
            String dayString = dateFormat.format(day.getTime());


            byte[] temp = new byte[masterKey.getEncoded().length + facilityIdentifierBytes.length + dayString.getBytes().length];

            ByteBuffer buff = ByteBuffer.wrap(temp);
            buff.put(masterKey.getEncoded());
            buff.put(facilityIdentifierBytes);
            buff.put(dayString.getBytes());

            byte[] secretKey = buff.array();
            secretKeys.put(day,secretKey);
        }

        return secretKeys;
    }

    private ArrayList<Calendar> generateDaysInMonth(int year, int monthIndex) {
        ArrayList<Calendar> daysInMonth = new ArrayList<>();

        // Set the calendar to the selected month
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthIndex);

        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= maxDay; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            daysInMonth.add((Calendar) cal.clone());
        }

        return daysInMonth;
    }


    ///////////////////////////////////////////////////////////////////
    ///         USER ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    @Override
    public void registerUser(int userIdentifier) throws SQLException, IllegalArgumentException {
        try {
            //Test if the identifier has the correct syntax
            if (testUserIdentifierSyntax(userIdentifier) == false) throw new IllegalArgumentException(
                    "User can't be created: The user identifier is not the correct syntax");

            //Test if the Catering facility doesn't already exist
            if (dbConnection.containsUser(userIdentifier) == true) throw new IllegalArgumentException(
                    "User can't be created: the user identifier already exists");

            //Place the new user in the db
            dbConnection.registerUser(userIdentifier);

            System.out.println("New user registered: " + userIdentifier);
        } catch (Exception e) {
            handleException(e);
            throw e;
        }

    }

    private boolean testUserIdentifierSyntax(int userIdentifier) {
        //Test if the identifier is 10 numbers long
        if (String.valueOf(userIdentifier).length() != 10) return false;

        return true;
    }

    @Override
    public byte[] getTokens(int userIdentifier, Calendar date) {
        return new byte[0];
    }



}
