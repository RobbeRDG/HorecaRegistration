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
    public void registerCateringFacility(String facilityIdentifier) throws Exception {
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


    private boolean testFacilityIdentifier(String facilityIdentifier) {
        //Test if the identifier is numeric
        try {
            Integer.parseInt(facilityIdentifier);
        } catch ( NumberFormatException e) {
            return false;
        }

        //Test if the identifier is 10 numbers long
        if (facilityIdentifier.length() != 10) return false;

        return true;
    }

    @Override
    public HashMap<LocalDate, byte[]> getPseudomyms(String facilityIdentifier, int monthIndex) throws Exception {
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
        ArrayList<String> daysInMonth = generateDaysInMonth(monthIndex);

        //Generate new secret key for each day in month
        HashMap<String,byte[]> facilitySecretKeys = generateSecretKeys(daysInMonth, facilityIdentifier);

        //Hash the secret keys
        ArrayList<byte[]> facilityPseudonyms = generatePseudonyms(facilitySecretKeys, facilityIdentifier);

    }

    private ArrayList<byte[]> generatePseudonyms(HashMap<String,byte[]> facilitySecretKeys, String facilityIdentifier) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        for
        byte[] encodedhash = digest.digest();
    }

    private HashMap<String, byte[]> generateSecretKeys(ArrayList<String> daysInMonth, String facilityIdentifier) {
        HashMap<String,byte[]> secretKeys = new HashMap<>();

        byte[] facilityIdentifierBytes = facilityIdentifier.getBytes();
        for (String day : daysInMonth) {
            byte[] temp = new byte[masterKey.getEncoded().length + facilityIdentifierBytes.length + day.getBytes().length];

            ByteBuffer buff = ByteBuffer.wrap(temp);
            buff.put(masterKey.getEncoded());
            buff.put(facilityIdentifierBytes);
            buff.put(day.getBytes());

            byte[] secretKey = buff.array();
            secretKeys.put(day,secretKey);
        }

        return secretKeys;
    }

    private ArrayList<String> generateDaysInMonth(int monthIndex) {
        ArrayList<String > daysInMonth = new ArrayList<>();

        // Set the calendar to the selected month
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, monthIndex);

        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        for (int i = 1; i <= maxDay; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            daysInMonth.add(df.format(cal.getTime()));
        }

        return daysInMonth;
    }


    ///////////////////////////////////////////////////////////////////
    ///         USER ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    @Override
    public void registerUser(String userIdentifier) {

    }

    @Override
    public byte[] getToken(String userIdentifier) {
        return new byte[0];
    }



}
