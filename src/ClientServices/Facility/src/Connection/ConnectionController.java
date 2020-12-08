package Connection;

import Common.Messages.PseudonymUpdate;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface ConnectionController {
    void connectToServices() throws RemoteException, NotBoundException;

    PseudonymUpdate getPseudonyms(String facilityIdentifier, int year, int monthIndex) throws Exception;

    void registerCateringFacility(String facilityIdentifier) throws Exception;
}
