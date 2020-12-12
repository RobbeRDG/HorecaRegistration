package Connection;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface ConnectionController {
    void startClientConnections() throws RemoteException, NotBoundException;
}
