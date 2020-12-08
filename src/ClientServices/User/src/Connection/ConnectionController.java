package Connection;

import Common.Messages.TokenUpdate;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface ConnectionController{
    void connectToServices() throws RemoteException, NotBoundException;
    void registerUSer(String userIdentifier) throws Exception;
    TokenUpdate getTodaysTokens(String userIdentifier) throws Exception;
}
