package Controller;

import Exceptions.AlreadyRegisteredException;
import Exceptions.NotRegisteredException;
import GUI.AppController;
import Messages.PseudonymUpdate;
import Messages.TokenUpdate;
import Connection.ConnectionController;
import Connection.ConnectionControllerImpl;
import Data.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RegistrarControllerImpl extends Application implements RegistrarController {
    private static DBConnection dbConnection;
    private static ConnectionController connectionController;
    private static PrivateKey masterKeyPrivate;
    private static PublicKey masterKeyPublic;
    private static final int secretKeyLength = 128;
    private static final int secretKeyIterations = 1000;
    private static final int saltLength = 16;
    private static final int numberOfTokens = 48;
    private static final int tokenLength = 16;
    private static Stage primaryStage;
    private static AppController appController;
    private static Pane appPane;

    ///////////////////////////////////////////////////////////////////
    ///         REGISTRAR LOGIC
    ///////////////////////////////////////////////////////////////////

    private static PublicKey readPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/registrar/registrarPublic.txt");
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
        InputStream in = new FileInputStream("Resources/private/keys/registrar/registrarPrivate.txt");
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
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            launch(args);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            System.out.println("Starting Registar service...");

            this.primaryStage = primaryStage;

            //Setup variables
            setUpControllerVariables();

            //load the app gui controller
            loadControllers();

            //Connect to the database
            dbConnection.connectToDatabase();

            //Start the server and client connections
            connectionController.startServerConnections();

            //Sleep for 10 sec
            Thread.sleep(10000);

            //Connect to the servers
            connectionController.startClientConnections();

            //Show the app screen
            showApp();

            //load the db capsules in the GUI
            appController.showAuthenticatedUsers(dbConnection.getAllRegisteredUsers());
            appController.showAuthenticatedFacilities(dbConnection.getAllRegisteredFacilities());

            System.out.println("Registrar ready");
        } catch (Exception e){
            handleException(e);
        }
    }

    public void showApp() throws IOException {
        //Display the chat fxml file
        Scene scene = new Scene(appPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadControllers() throws IOException {
        //Load the app controller
        FXMLLoader appLoader = new FXMLLoader(getClass().getResource("../GUI/App.fxml"));
        appPane = appLoader.load();

        //load the controller and pass the chatRoom and listener
        appController = (AppController) appLoader.getController();
        //Set the ClientSide.GUI Controller
        appController.setRegistrarController(this);
    }

    private void setUpControllerVariables() throws InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException, IOException {
        if (dbConnection == null) dbConnection = new DBConnection();
        if (connectionController == null) connectionController = new ConnectionControllerImpl(this);
        if (masterKeyPrivate == null || masterKeyPublic == null) {
            masterKeyPublic = readPublicKey();
            masterKeyPrivate = readPrivateKey();
        }
    }


    @Override
    public void refreshPrimaryStage() {
        primaryStage.show();
    }

    public void stop() {
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

    private static void handleException(Exception e) {
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
            if (dbConnection.containsCateringFacility(facilityIdentifier)) throw new AlreadyRegisteredException(
                    "Catering facility can't be created: the catering identifier already exists");

            //Place the new facility in the db
            dbConnection.registerCateringFacility(facilityIdentifier);

            //Reload the facility table in the GUI
            appController.showAuthenticatedFacilities(dbConnection.getAllRegisteredFacilities());

            System.out.println("New facility registered: " + facilityIdentifier);
        } catch (IllegalArgumentException | AlreadyRegisteredException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw e;
        }
    }


    private boolean testFacilityIdentifier(String facilityIdentifier) {
        //test if the identifier is a number
        try {
            Integer.parseInt(facilityIdentifier);
        } catch (NumberFormatException e) {
            return false;
        }

        //Test if the identifier is 10 numbers long
        if (facilityIdentifier.length() != 10) return false;

        return true;
    }

    @Override
    public PseudonymUpdate getPseudomyms(String facilityIdentifier, int year, int monthIndex) throws Exception {
        try {
            //test if the facility is registered
            if (!dbConnection.containsCateringFacility(facilityIdentifier)) throw new NotRegisteredException("Couldn't create pseudonyms: Facility not registered");

            //create a pseudonym hashmap
            HashMap<LocalDate,byte[]> facilityPseudonyms = new HashMap<>();


            try {
                //Search database for existing pseudonyms
                facilityPseudonyms = dbConnection.getPseudonyms(facilityIdentifier, year, monthIndex);
            } catch (IllegalArgumentException e) {
                //Create new pseudonyms
                //Get the days in the month
                ArrayList<LocalDate> daysInMonth = generateDaysInMonth(year, monthIndex);

                //Generate new secret key for each day in month
                HashMap<LocalDate,byte[]> facilitySecretKeys = generateSecretKeys(daysInMonth, facilityIdentifier);

                //Hash the secret keys to find the pseudonyms
                facilityPseudonyms = generatePseudonyms(facilitySecretKeys, facilityIdentifier);

                //Place the pseudonyms in the db
                dbConnection.addPseudonyms(facilityIdentifier, facilityPseudonyms);
            }

            return new PseudonymUpdate(facilityPseudonyms);
        } catch (NotRegisteredException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't create pseudonyms: Something went wrong");
        }
    }

    private HashMap<LocalDate,byte[]> generatePseudonyms(HashMap<LocalDate,byte[]> facilitySecretKeys, String facilityIdentifier) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] facilityIdentifierBytes = facilityIdentifier.getBytes();
        HashMap<LocalDate,byte[]> pseudonyms = new HashMap<>();

        for (Map.Entry<LocalDate, byte[]> entry : facilitySecretKeys.entrySet()) {
            LocalDate day = entry.getKey();
            byte[] secretKey = entry.getValue();

            //Generate string from date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String dayString = day.format(formatter);

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

    private HashMap<LocalDate, byte[]> generateSecretKeys(ArrayList<LocalDate> daysInMonth, String facilityIdentifier) throws NoSuchAlgorithmException, InvalidKeySpecException {
        HashMap<LocalDate,byte[]> secretKeys = new HashMap<>();

        byte[] facilityIdentifierBytes = facilityIdentifier.getBytes();
        for (LocalDate day : daysInMonth) {
            //Generate string from date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String dayString = day.format(formatter);


            byte[] temp = new byte[masterKeyPrivate.getEncoded().length + facilityIdentifierBytes.length + dayString.getBytes().length];

            ByteBuffer buff = ByteBuffer.wrap(temp);
            buff.put(masterKeyPrivate.getEncoded());
            buff.put(facilityIdentifierBytes);
            buff.put(dayString.getBytes());

            byte[] secretKeyStarter = buff.array();

            //Generate a derived secret key from the starter
            char[] secretKeyStarterChars = Base64.getEncoder().encodeToString(secretKeyStarter).toCharArray();
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[saltLength];
            random.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(secretKeyStarterChars, salt, secretKeyIterations, secretKeyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] secretKey = skf.generateSecret(spec).getEncoded();

            secretKeys.put(day,secretKey);
        }

        return secretKeys;
    }

    private ArrayList<LocalDate> generateDaysInMonth(int year, int monthIndex) {
        ArrayList<LocalDate> daysInMonth = new ArrayList<>();

        // get the number of days in month
        YearMonth yearMonthObject = YearMonth.of(year, monthIndex);
        int maxDay = yearMonthObject.lengthOfMonth();

        for (int i = 1; i <= maxDay; i++) {
            LocalDate date = LocalDate.of(year, monthIndex, i);
            daysInMonth.add(date);
        }

        return daysInMonth;
    }


    ///////////////////////////////////////////////////////////////////
    ///         USER ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    @Override
    public void registerUser(String userIdentifier) throws SQLException, IllegalArgumentException, AlreadyRegisteredException {
        try {
            //Test if the identifier has the correct syntax
            if (!testUserIdentifierSyntax(userIdentifier)) throw new IllegalArgumentException(
                    "User can't be created: The user identifier is not the correct syntax");

            //Test if the Catering facility doesn't already exist
            if (dbConnection.containsUser(userIdentifier)) throw new AlreadyRegisteredException(
                    "User can't be created: the user identifier already exists");

            //Place the new user in the db
            dbConnection.registerUser(userIdentifier);

            //Reload the user table in GUI
            appController.showAuthenticatedUsers(dbConnection.getAllRegisteredUsers());

            System.out.println("New user registered: " + userIdentifier);
        } catch (IllegalArgumentException | AlreadyRegisteredException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw e;
        }

    }

    private boolean testUserIdentifierSyntax(String userIdentifier) {
        //test if the identifier is a number
        try {
            Integer.parseInt(userIdentifier);
        } catch (NumberFormatException e) {
            return false;
        }

        //Test if the identifier is 10 numbers long
        if (userIdentifier.length() != 10) return false;

        return true;
    }

    @Override
    public TokenUpdate getTokens(String userIdentifier, LocalDate date) throws Exception {
        ArrayList<byte[]> tokens = new ArrayList<>();
        ArrayList<byte[]> signatures = new ArrayList<>();

        try {
            //test if the user is registered
            if (!dbConnection.containsUser(userIdentifier)) throw new NotRegisteredException("Couldn't create tokens: user not registered");

            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(masterKeyPrivate);

            try {
                //Test if tokens are already created
                tokens = dbConnection.getTokens(userIdentifier, date);
            } catch (IllegalArgumentException e) {
                //If no tokens already exist for today, generate them
                SecureRandom secureRandom = new SecureRandom();
                for (int i = 0; i < numberOfTokens; i++) {
                    //Create the random token
                    byte[] token = new byte[tokenLength];
                    secureRandom.nextBytes(token);

                    //Add the token to the arraylist
                    tokens.add(token);
                }

                //Save the generated tokens in the db
                dbConnection.addTokens(userIdentifier, date, tokens);

                //Send the generated tokens to the mixing proxy
                connectionController.addTokensToMixingProxy(date, tokens);
            }

            //Create the signature for the tokens
            for ( byte[] token : tokens) {
                sign.update(token);
                byte[] signature = sign.sign();

                //Place the token and signature in signatures
                signatures.add(signature);
            }

            //Return a token update message to the user
            TokenUpdate tokenUpdate = new TokenUpdate(tokens, signatures);
            return tokenUpdate;
        } catch (NotRegisteredException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't create new tokens: Something went wrong");
        }
    }

    ///////////////////////////////////////////////////////////////////
    ///         MATCHING SERVICE LOGIC
    ///////////////////////////////////////////////////////////////////

    @Override
    public byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws Exception {
        try {
            return dbConnection.getFacilityPseudonym(facilityIdentifier, date);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't fetch facility pseudonym: Something went wrong");
        }
    }

    @Override
    public void addUnacknowledgedTokens(ArrayList<byte[]> unacknowledgedTokens) throws Exception {
        try {
            dbConnection.addUnacknowledgedTokens(unacknowledgedTokens);
        } catch (Exception e) {
            throw new Exception("Couldn't add unacknowledged tokens: Something went wrong");
        }
    }


}
