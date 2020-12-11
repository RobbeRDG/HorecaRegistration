package Connection;

import Common.Messages.InfectedUserMessage;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface ConnectionController {
    void startServerConnections() throws RemoteException;
    void startClientConnections();

    void addInfectedUser(InfectedUserMessage infectedUserMessage) throws Exception;

    public byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws SQLException;
}
