package Connection;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.HashMap;

public interface Connection extends Remote {
    void registerCateringFacility(String barIdentifier) throws Exception;
    HashMap<LocalDate, byte[]> getPseudonyms(String barIdentifier) throws RemoteException;
}
