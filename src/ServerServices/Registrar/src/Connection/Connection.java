package Connection;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;

public interface Connection extends Remote {
    void registerCateringFacility(int barIdentifier) throws Exception;
    HashMap<Calendar, byte[]> getPseudonyms(int barIdentifier, int year, int monthIndex) throws Exception;
    void registerUser(int userIdentifier) throws Exception;
    byte[] getTokens(int userIdentifier, Calendar date);
}
