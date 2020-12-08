package Connection;

import Common.Exceptions.AlreadyRegisteredException;
import Common.Messages.PseudonymUpdate;
import Common.Messages.TokenUpdate;
import Connection.Registrar.Facility.RegistrarFacilityService;
import Connection.Registrar.Facility.RegistrarFacilityServiceImpl;
import Connection.Registrar.User.RegistrarUserService;
import Connection.Registrar.User.RegistrarUserServiceImpl;
import Controller.RegistrarController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;

public class ConnectionControllerImpl implements ConnectionController{
    private static RegistrarController registrarController;
    private static RegistrarFacilityService registrarFacilityServer;
    private static RegistrarUserService registrarUserServer;
    private static final int registrarFacilityRMIPort = 2222;
    private static final int registrarUserRMIPort = 3333;

    public ConnectionControllerImpl(RegistrarController registrarController) {
        this.registrarController = registrarController;
        registrarFacilityServer = new RegistrarFacilityServiceImpl(this);
        registrarUserServer = new RegistrarUserServiceImpl(this);
    }

    public void startServerConnections() throws RemoteException {
        //Start the facility server
        RegistrarFacilityService facilityStub = (RegistrarFacilityService) UnicastRemoteObject
                .exportObject((RegistrarFacilityService) registrarFacilityServer, 0);

        Registry facilityRegistry = LocateRegistry.createRegistry(registrarFacilityRMIPort);
        facilityRegistry.rebind("RegistrarFacilityService", facilityStub);

        //Start the user server
        RegistrarUserService userStub = (RegistrarUserService) UnicastRemoteObject
                .exportObject((RegistrarUserService) registrarUserServer, 0);

        Registry userRegistry = LocateRegistry.createRegistry(registrarUserRMIPort);
        userRegistry.rebind("RegistrarUserService", userStub);

        System.out.println("Started RMI servers");
    }

    @Override
    public void registerCateringFacility(String facilityIdentifier) throws Exception {
        registrarController.registerCateringFacility(facilityIdentifier);
    }

    @Override
    public PseudonymUpdate getPseudomyms(String facilityIdentifier, int year, int monthIndex) throws Exception {
        return registrarController.getPseudomyms(facilityIdentifier, year, monthIndex);
    }

    @Override
    public void registerUser(String userIdentifier) throws SQLException, AlreadyRegisteredException {
        registrarController.registerUser(userIdentifier);
    }

    @Override
    public TokenUpdate getTokens(String userIdentifier, LocalDate date) throws Exception {
        return registrarController.getTokens(userIdentifier, date);
    }
}
