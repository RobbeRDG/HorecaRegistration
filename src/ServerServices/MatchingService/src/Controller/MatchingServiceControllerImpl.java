package Controller;

import Common.Exceptions.NotValidException;
import Common.Messages.InfectedUserMessage;
import Common.Objects.CapsuleLog;
import Common.Objects.FacilityVisitLog;
import Connection.ConnectionController;
import Connection.ConnectionControllerImpl;
import Controller.HelperObjects.SendUnacknowledgedTokensCaller;
import Data.DBConnection;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

public class MatchingServiceControllerImpl implements MatchingServiceController{
    private static DBConnection dbConnection;
    private static ConnectionController connectionController;
    private static PrivateKey mixingProxyPrivateKey;
    private static PublicKey mixingProxyPublicKey;
    private static PublicKey practitionerPublicKey;
    private static final int uninformedRevealPeriodInDays = 1;

    ///////////////////////////////////////////////////////////////////
    ///         MIXING PROXY INTERNAL LOGIC
    ///////////////////////////////////////////////////////////////////

    public MatchingServiceControllerImpl() throws InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException, IOException {
        if (dbConnection == null) dbConnection = new DBConnection();
        if (connectionController == null) connectionController = new ConnectionControllerImpl(this);
        if (mixingProxyPrivateKey == null || mixingProxyPublicKey == null || practitionerPublicKey == null) {
            mixingProxyPublicKey = readPublicKey();
            mixingProxyPrivateKey = readPrivateKey();
            practitionerPublicKey = readPractitionerPublicKey();
        }
    }

    private static PublicKey readPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/matchingService/matchingServicePublic.txt");
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
        InputStream in = new FileInputStream("Resources/private/keys/matchingService/matchingServicePrivate.txt");
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

    private static PublicKey readPractitionerPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/practitioner/practitionerPublic.txt");
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

    public static void main(String[] args){
        try {
            MatchingServiceControllerImpl matchingService = new MatchingServiceControllerImpl();
            matchingService.start();
            //registrar.stop();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void start() {
        try {
            System.out.println("Starting Matching service...");

            //Connect to the database
            dbConnection.connectToDatabase();

            //Start the connection server
            connectionController.startServerConnections();

            System.out.println("Matching service ready");
        } catch (Exception e){
            handleException(e);
        }
    }

    private void stop() {
        int status = 0;
        try {
            System.out.println("Stopping Matching service...");
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

    @Override
    public void addInfectedUser(InfectedUserMessage infectedUserMessage) throws Exception {
        try {
            //Test if the message signature is valid
            if (!isValidMessageSignature(infectedUserMessage)) throw new SignatureException(
                    "Couldn't insert infected user: message signature doesn't match");

            //Test if the facility visits in the message itself are valid
            if (!containsValidFacilityVisits(infectedUserMessage)) throw new NotValidException(
                    "Couldn't insert infected user: logs contain invalid facility");

            //Set the critical capsules with the visited facility logs
            dbConnection.markCriticalCapsules(infectedUserMessage.getInfectedUser().getInfectedFacilityIntervals());


            //Set the infected user tokens to informed
            dbConnection.markInformed(infectedUserMessage.getInfectedUser().getInfectedTokens());
        } catch (SignatureException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't insert infected user: something went wrong");
        }
    }

    public void sendUnacknowledgedTokens() {
        try {
            ArrayList<byte[]> unacknowledgedTokens = dbConnection.getUnacknowledgedTokens(LocalDate.now().minusDays(uninformedRevealPeriodInDays));
            connectionController.addUnacknowledgedTokens(unacknowledgedTokens);

            //Set the send tokens as acknowledged
            dbConnection.markInformed(unacknowledgedTokens);

            //Set a timer task to send the unacknowledged tokens to the registrar
            java.util.Date taskDate = Date.from(LocalDateTime.now().plusDays(uninformedRevealPeriodInDays).atZone(ZoneId.systemDefault()).toInstant());
            Timer sendUninformedTimer = new Timer();
            sendUninformedTimer.schedule(new SendUnacknowledgedTokensCaller(this), taskDate );
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void addCapsules(ArrayList<CapsuleLog> capsules) throws Exception {
        try {
            dbConnection.addCapsules(capsules);
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't upload the capsule logs: Something went wrong");
        }
    }

    @Override
    public void submitAcknowledgements(ArrayList<byte[]> acknowledgementTokens) throws Exception {
        try {
            dbConnection.markInformed(acknowledgementTokens);
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't acknowledge tokens: Something went wrong");
        }
    }

    @Override
    public ArrayList<CapsuleLog> getInfectedCapsules() throws Exception {
        try {
            return dbConnection.getInfectedCapsules();
        } catch (Exception e) {
            throw new Exception("Couldn't fetch infected capsules: something went wrong");
        }
    }

    private boolean containsValidFacilityVisits(InfectedUserMessage infectedUserMessage) throws Exception {
        ArrayList<FacilityVisitLog> facilityVisitLogs = infectedUserMessage.getInfectedUser().getInfectedFacilityIntervals();

        for (FacilityVisitLog facilityVisitLog : facilityVisitLogs) {
            //Extract the information from the log
            String facilityIdentifier = facilityVisitLog.getVisitedFacility().getFacilityIdentifier();
            LocalDate visitedDate = facilityVisitLog.getEntryTime().toLocalDate();
            byte [] randomKey = facilityVisitLog.getVisitedFacility().getRandomKey();
            byte [] facilityKey = facilityVisitLog.getVisitedFacility().getFacilityKey();

            //Get the pseudonym from the visited facility on the visit day
            byte[] facilityPseudonym = connectionController.getFacilityPseudonym(facilityIdentifier , visitedDate );

            //Hash the random key and facility pseudonym
            byte[] hashedPseudonym = hashPseudonym(facilityPseudonym, randomKey);

            //The log is correct if the hash from the randomKey and pseudonym equals the facilityKey
            if (!Arrays.equals(hashedPseudonym, facilityKey)) return false;
        }

        return true;
    }

    private byte[] hashPseudonym(byte[] facilityPseudonym, byte[] randomKey) throws NoSuchAlgorithmException {
        byte[] temp = new byte[facilityPseudonym.length + randomKey.length];

        ByteBuffer buff = ByteBuffer.wrap(temp);
        buff.put(facilityPseudonym);
        buff.put(randomKey);

        byte[] combinedPreHash = buff.array();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(combinedPreHash);
    }

    private boolean isValidMessageSignature(InfectedUserMessage infectedUserMessage) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException {
        byte[] signature = infectedUserMessage.getSignature();

        Signature sign = Signature.getInstance("SHA256witRSA");
        sign.initVerify(practitionerPublicKey);
        sign.update(infectedUserMessage.getInfectedUserBytes());
        return sign.verify(signature);
    }

}
