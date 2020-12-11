package Controller;

import Common.Exceptions.CapsuleNotValidException;
import Common.Messages.CapsuleVerification;
import Common.Objects.CapsuleLog;
import Common.Objects.Token;
import Connection.ConnectionController;
import Connection.ConnectionControllerImpl;
import Data.DBConnection;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MixingProxyControllerImpl implements MixingProxyController{
    private static DBConnection dbConnection;
    private static ConnectionController connectionController;
    private static PrivateKey mixingProxyPrivateKey;
    private static PublicKey mixingProxyPublicKey;

    ///////////////////////////////////////////////////////////////////
    ///         MIXING PROXY INTERNAL LOGIC
    ///////////////////////////////////////////////////////////////////

    public MixingProxyControllerImpl() throws InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException, IOException {
        if (dbConnection == null) dbConnection = new DBConnection();
        if (connectionController == null) connectionController = new ConnectionControllerImpl(this);
        if (mixingProxyPrivateKey == null || mixingProxyPublicKey == null) {
            mixingProxyPublicKey = readPublicKey();
            mixingProxyPrivateKey = readPrivateKey();
        }
    }

    private static PublicKey readPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/mixingProxy/mixingProxyPublic.txt");
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
        InputStream in = new FileInputStream("Resources/private/keys/mixingProxy/mixingProxyPublic.txt");
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
            MixingProxyControllerImpl mixingProxy = new MixingProxyControllerImpl();
            mixingProxy.start();
            //registrar.stop();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void start() {
        try {
            System.out.println("Starting Mixing proxy service...");

            //Connect to the database
            dbConnection.connectToDatabase();

            //Start the connection server
            connectionController.startServerConnections();

            System.out.println("Mixing proxy ready");
        } catch (Exception e){
            handleException(e);
        }
    }

    private void stop() {
        int status = 0;
        try {
            System.out.println("Stopping Mixing proxy service...");
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
    ///         TOKEN REGISTRATION
    ///////////////////////////////////////////////////////////////////

    @Override
    public CapsuleVerification registerToken(CapsuleLog capsuleLog) throws Exception {
        try {
            //Check if the token is valid
            if (!validateCapsule(capsuleLog)) throw new CapsuleNotValidException("The send capsule is not a valid capsule");

            //Check if the capsule is not already registered
            if (dbConnection.containsCapsule(capsuleLog)) throw new CapsuleNotValidException("Capsule is already registered");

            //Place the capsule in the db
            dbConnection.addCapsule(capsuleLog);

            //Return the signed facility key
            return generateCapsuleVerification(capsuleLog);
        } catch (CapsuleNotValidException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't register tokens : Something went wrong");
        }

    }

    private CapsuleVerification generateCapsuleVerification(CapsuleLog capsuleLog) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(mixingProxyPrivateKey);

        //Get the facility key
        byte[] facilityKey = capsuleLog.getFacilityKey();

        //Sign the facility key
        sign.update(facilityKey);
        byte[] keySignature = sign.sign();

        return new CapsuleVerification(facilityKey, keySignature);
    }

    @Override
    public void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception {
        try {
            dbConnection.addTokens(date, tokens);
        } catch (Exception e) {
            handleException(e);;
        }
    }

    private boolean validateCapsule(CapsuleLog capsuleLog) throws SQLException {
        //Extract the capsule contents
        Token token = capsuleLog.getToken();
        LocalDateTime startTime = capsuleLog.getStartTime();
        LocalDateTime stopTime = capsuleLog.getStopTime();
        byte[] facilityKey = capsuleLog.getFacilityKey();

        //Get the tokenbytes and date from the valid tokens table
        ResultSet rs = dbConnection.getValidToken(token.getTokenBytes(), token.getDate());

        if (rs.next()) {
            //Test if the startTime is on the day of the valid token
            LocalDate validDate = rs.getDate("date").toLocalDate();
            LocalDate startDate = startTime.toLocalDate();

            return startDate.isEqual(validDate);
        } else return false;

    }
}
