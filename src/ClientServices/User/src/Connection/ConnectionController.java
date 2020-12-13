package Connection;

import Messages.CapsuleVerification;
import Messages.TokenUpdate;
import Objects.CapsuleLog;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ConnectionController{
    void startClientConnections() throws RemoteException, NotBoundException;
    void registerUSer(String userIdentifier) throws Exception;
    TokenUpdate getTodaysTokens(String userIdentifier) throws Exception;
    CapsuleVerification registerCapsule(CapsuleLog capsuleLog) throws Exception;

    ArrayList<CapsuleLog> getInfectedCapsules() throws Exception;

    void acknowledgeTokens(ArrayList<byte[]> acknowledgeTokens) throws Exception;
}
