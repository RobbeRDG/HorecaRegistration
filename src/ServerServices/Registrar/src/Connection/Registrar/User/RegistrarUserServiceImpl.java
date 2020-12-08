package Connection.Registrar.User;

import Common.Messages.TokenUpdate;
import Connection.ConnectionController;

import java.time.LocalDate;

public class RegistrarUserServiceImpl implements RegistrarUserService {
    private static ConnectionController connectionController;

    public RegistrarUserServiceImpl(ConnectionController registarController) {
        if (this.connectionController == null) this.connectionController = registarController;
    }

    ///////////////////////////////////////////////////////////////////
    ///         USER ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    @Override
    public void registerUser(String userIdentifier) throws Exception {
        connectionController.registerUser(userIdentifier);
    }

    @Override
    public TokenUpdate getTokens(String userIdentifier, LocalDate date) throws Exception {
        return connectionController.getTokens(userIdentifier, date);
    }
}
