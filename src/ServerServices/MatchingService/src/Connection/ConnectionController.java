package Connection;

import Messages.InfectedUserMessage;
import Objects.CapsuleLog;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface ConnectionController {
    void startServerConnections() throws RemoteException;
    void startClientConnections() throws RemoteException, NotBoundException;

    void addInfectedUser(InfectedUserMessage infectedUserMessage) throws Exception;

    public byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws Exception;

    void addCapsules(ArrayList<CapsuleLog> capsules) throws Exception;

    void submitAcknowledgements(ArrayList<byte[]> acknowledgementTokens) throws Exception;

    ArrayList<CapsuleLog> getInfectedCapsules() throws Exception;

    void addUnacknowledgedTokens(ArrayList<byte[]> unacknowledgedTokens) throws Exception;
}
