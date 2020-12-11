package Connection;

import Common.Messages.CapsuleVerification;
import Common.Objects.CapsuleLog;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface ConnectionController {
    void startServerConnections() throws RemoteException;

    CapsuleVerification registerToken(CapsuleLog capsuleLog) throws Exception;

    void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception;
}
