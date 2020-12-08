package Connection;

import Common.Exceptions.AlreadyRegisteredException;
import Common.Messages.PseudonymUpdate;
import Common.Messages.TokenUpdate;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;

public interface ConnectionController {
    void startServerConnections() throws RemoteException;

    void registerCateringFacility(String facilityIdentifier) throws Exception;

    PseudonymUpdate getPseudomyms(String facilityIdentifier, int year, int monthIndex) throws Exception;

    void registerUser(String userIdentifier) throws SQLException, AlreadyRegisteredException;

    TokenUpdate getTokens(String userIdentifier, LocalDate date) throws Exception;
}
