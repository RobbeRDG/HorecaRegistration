package Connection;

import Common.Exceptions.CapsuleNotValidException;
import Common.Messages.CapsuleVerification;
import Common.Objects.Capsule;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface ConnectionController {
    void startServerConnections() throws RemoteException;

    CapsuleVerification registerToken(Capsule capsule) throws Exception;

    void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception;
}
