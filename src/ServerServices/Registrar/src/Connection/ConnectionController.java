package Connection;

import Common.Exceptions.AlreadyRegisteredException;
import Common.Messages.PseudonymUpdate;
import Common.Messages.TokenUpdate;
import Common.Objects.Token;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface ConnectionController {
    void startServerConnections() throws RemoteException;

    void registerCateringFacility(String facilityIdentifier) throws Exception;

    PseudonymUpdate getPseudomyms(String facilityIdentifier, int year, int monthIndex) throws Exception;

    void registerUser(String userIdentifier) throws SQLException, AlreadyRegisteredException;

    TokenUpdate getTokens(String userIdentifier, LocalDate date) throws Exception;

    void startClientConnections() throws RemoteException, NotBoundException;

    void addTokensToMixingProxy(LocalDate date, ArrayList<byte[]> tokens) throws Exception;

    byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws SQLException;
}
