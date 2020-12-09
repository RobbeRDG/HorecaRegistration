package Controller;

import Connection.ConnectionController;
import Connection.ConnectionControllerImpl;
import Data.DBConnection;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class MixingProxyControllerImpl implements MixingProxyController{
    private static DBConnection dbConnection;
    private static ConnectionController connectionController;
    private static PrivateKey mixingProxyPrivateKey;
    private static PublicKey mixingProxyPublicKey;

    ///////////////////////////////////////////////////////////////////
    ///         MIXING PROXY LOGIC
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
            System.out.println("Mixing proxy failed");
            e.printStackTrace();
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
            System.out.println("Startup failed: " + e.getMessage());
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

    private void handleException(Exception e) {
        System.out.println(e.getMessage());
    }


}
