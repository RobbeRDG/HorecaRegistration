package Connection;

import Common.Messages.TokenUpdate;
import Connection.Registrar.Facility.RegistrarFacilityService;
import Connection.Registrar.User.RegistrarUserService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;

public class ConnectionControllerImpl implements ConnectionController{
    private static final int registrarUserServerPort = 3333;
    private static RegistrarUserService registrarUserService;

    public ConnectionControllerImpl() {
    }

    @Override
    public void connectToServices() throws RemoteException, NotBoundException {
        //Connect to the user service
        Registry registrarUserRegister = LocateRegistry.getRegistry("localhost", registrarUserServerPort);
        registrarUserService = (RegistrarUserService) registrarUserRegister
                .lookup("RegistrarUserService");
    }

    @Override
    public void registerUSer(String userIdentifier) throws Exception {
        registrarUserService.registerUser(userIdentifier);
    }

    @Override
    public TokenUpdate getTodaysTokens(String userIdentifier) throws Exception {
        LocalDate today = LocalDate.now();
        return registrarUserService.getTokens(userIdentifier, today);
    }
}
