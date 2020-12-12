package Connection;

import Common.Messages.InfectedUserMessage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface ConnectionController {
    void startClientConnections() throws RemoteException, NotBoundException;
    void addInfectedUser(InfectedUserMessage message) throws Exception;
}
