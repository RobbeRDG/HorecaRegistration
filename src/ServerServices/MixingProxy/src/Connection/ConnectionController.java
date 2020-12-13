package Connection;

import Messages.CapsuleVerification;
import Objects.CapsuleLog;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface ConnectionController {
    void startServerConnections() throws RemoteException;

    CapsuleVerification registerToken(CapsuleLog capsuleLog) throws Exception;

    void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception;

    public void startClientConnections() throws RemoteException, NotBoundException;

    void flushCapsules(ArrayList<CapsuleLog> capsuleLogs) throws Exception;

    void acknowledgeTokens(ArrayList<byte[]> acknowledgeTokens) throws Exception;

    void forwardAcknowledgeTokens(ArrayList<byte[]> acknowledgeTokens) throws Exception;
}
