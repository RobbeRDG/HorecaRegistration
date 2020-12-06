package Connection;

import Common.Messages.TokenUpdate;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;

public interface Connection extends Remote {
    void registerCateringFacility(int barIdentifier) throws Exception;
    HashMap<Calendar, byte[]> getPseudonyms(int barIdentifier, int year, int monthIndex) throws Exception;
    void registerUser(int userIdentifier) throws Exception;
    TokenUpdate getTokens(int userIdentifier, Calendar date) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, Exception;
}
