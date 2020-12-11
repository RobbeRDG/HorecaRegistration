package Connection;

import Common.Messages.CapsuleVerification;
import Common.Messages.TokenUpdate;
import Common.Objects.CapsuleLog;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface ConnectionController{
    void startClientConnections() throws RemoteException, NotBoundException;
    void registerUSer(String userIdentifier) throws Exception;
    TokenUpdate getTodaysTokens(String userIdentifier) throws Exception;
    CapsuleVerification registerCapsule(CapsuleLog capsuleLog) throws Exception;
}
