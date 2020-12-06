package Controller;

import Common.Messages.TokenUpdate;
import Connection.*;
import Data.DBConnection;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrarControllerImpl implements RegistarController{
    private static DBConnection dbConnection;
    private static ConnectionImpl rmiServer;
    private static PrivateKey masterKeyPrivate;
    private static PublicKey masterKeyPublic;

    public RegistrarControllerImpl() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, CertificateException, ClassNotFoundException {
        if (dbConnection == null) dbConnection = new DBConnection();
        if (rmiServer == null) rmiServer = new ConnectionImpl(this);
        if (masterKeyPrivate == null || masterKeyPublic == null) {
            masterKeyPublic = readPublicKey();
            masterKeyPrivate = readPrivateKey();
        }
    }

    private static PublicKey readPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/registrar/registrarMasterPublic.txt");
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            throw e;
        } finally {
            oin.close();
        }
    }

    private static PrivateKey readPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/registrar/registrarMasterPrivate.txt");
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            throw e;
        } finally {
            oin.close();
        }
    }

    public static void main(String[] args){
        try {
            RegistrarControllerImpl registrar = new RegistrarControllerImpl();
            registrar.start();
            registrar.stop();
        } catch (Exception e) {
            System.out.println("Registrar failed");
            e.printStackTrace();
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
        try {
            //test if the facility is registered
            if (!dbConnection.containsCateringFacility(facilityIdentifier)) throw new IllegalArgumentException("Couldn't create pseudonyms: Facility not registered");

            //Get the days in the month
            ArrayList<Calendar> daysInMonth = generateDaysInMonth(year, monthIndex);

            //Generate new secret key for each day in month
            HashMap<Calendar,byte[]> facilitySecretKeys = generateSecretKeys(daysInMonth, facilityIdentifier);

            //Hash the secret keys to find the pseudonyms
            HashMap<Calendar,byte[]> facilityPseudonyms = generatePseudonyms(facilitySecretKeys, facilityIdentifier);

            //Place the pseudonyms in the db
            dbConnection.addPseudonyms(facilityIdentifier, facilityPseudonyms);

            return facilityPseudonyms;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't create pseudonyms: Something went wrong");
        }
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


            byte[] temp = new byte[masterKeyPrivate.getEncoded().length + facilityIdentifierBytes.length + dayString.getBytes().length];

            ByteBuffer buff = ByteBuffer.wrap(temp);
            buff.put(masterKeyPrivate.getEncoded());
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
    public TokenUpdate getTokens(int userIdentifier, Calendar date) throws Exception {
        try {
            //test if the user is registered
            if (!dbConnection.containsUser(userIdentifier)) throw new IllegalArgumentException("Couldn't create tokens: user not registered");

            ArrayList<byte[]> tokens = new ArrayList<>();
            HashMap<byte[], byte[]> signatures = new HashMap<>();
            SecureRandom secureRandom = new SecureRandom();

            //Generate set amount of random tokens
            int numberOfTokens = 48;
            int tokenLength = 16;

            for (int i = 0; i < numberOfTokens; i++) {
                //Create the random token
                byte[] token = new byte[tokenLength];
                secureRandom.nextBytes(token);

                //Create the signature for the token
                Signature sign = Signature.getInstance("SHA256withRSA");
                sign.initSign(masterKeyPrivate);
                sign.update(token);
                byte[] signature = sign.sign();

                //Place the token and signature in the arraylist and hashmap
                tokens.add(token);
                signatures.put(token, signature);
            }

            //Save the generated tokens in the db
            dbConnection.addTokens(userIdentifier, date, tokens);

            //Return a token update message to the user
            TokenUpdate tokenUpdate = new TokenUpdate(tokens, signatures);
            return tokenUpdate;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't create new tokens: Something went wrong");
        }
    }



}
