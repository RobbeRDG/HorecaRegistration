package Connection;

import Exceptions.AlreadyRegisteredException;
import Messages.PseudonymUpdate;
import Messages.TokenUpdate;
import RMIInterfaces.MixingProxy.MixingProxyRegistrarService;
import RMIInterfaces.Registrar.RegistrarFacilityService;
import Connection.Registrar.Facility.RegistrarFacilityServiceImpl;
import RMIInterfaces.Registrar.RegistrarMatchingService;
import Connection.Registrar.MatchingService.RegistrarMatchingServiceImpl;
import RMIInterfaces.Registrar.RegistrarUserService;
import Connection.Registrar.User.RegistrarUserServiceImpl;
import Controller.RegistrarController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ConnectionControllerImpl implements ConnectionController{
    private static RegistrarController registrarController;
    private static RegistrarFacilityService registrarFacilityServer;
    private static RegistrarUserService registrarUserServer;
    private static RegistrarMatchingService registrarMatchingServiceServer;
    private static MixingProxyRegistrarService mixingProxyRegistrarService;
    private static final int registrarFacilityRMIServerPort = 2222;
    private static final int registrarUserRMIServerPort = 3333;
    private static final int registrarMatchingServiceRMIServerPort = 9999;
    private static final int mixingProxyRegistrarRMIClientPort = 5555;

    public ConnectionControllerImpl(RegistrarController registrarController) {
        this.registrarController = registrarController;
        registrarFacilityServer = new RegistrarFacilityServiceImpl(this);
        registrarUserServer = new RegistrarUserServiceImpl(this);
        registrarMatchingServiceServer = new RegistrarMatchingServiceImpl(this);
    }

    public void startServerConnections() throws RemoteException {
        //Start the facility server
        RegistrarFacilityService facilityStub = (RegistrarFacilityService) UnicastRemoteObject
                .exportObject((RegistrarFacilityService) registrarFacilityServer, 0);

        Registry facilityRegistry = LocateRegistry.createRegistry(registrarFacilityRMIServerPort);
        facilityRegistry.rebind("RegistrarFacilityService", facilityStub);

        //Start the user server
        RegistrarUserService userStub = (RegistrarUserService) UnicastRemoteObject
                .exportObject((RegistrarUserService) registrarUserServer, 0);

        Registry userRegistry = LocateRegistry.createRegistry(registrarUserRMIServerPort);
        userRegistry.rebind("RegistrarUserService", userStub);

        //Start the Matching Service server
        RegistrarMatchingService matchingServiceStub = (RegistrarMatchingService) UnicastRemoteObject
                .exportObject((RegistrarMatchingService) registrarMatchingServiceServer, 0);

        Registry matchingServiceRegistry = LocateRegistry.createRegistry(registrarMatchingServiceRMIServerPort);
        matchingServiceRegistry.rebind("RegistrarMatchingService", matchingServiceStub);

        System.out.println("Started all RMI server instances");
    }

    public void startClientConnections() throws RemoteException, NotBoundException {
        //Connect to the mixing proxy service
        Registry mixingProxyRegistrarRegister = LocateRegistry.getRegistry("localhost", mixingProxyRegistrarRMIClientPort);
        mixingProxyRegistrarService = (MixingProxyRegistrarService) mixingProxyRegistrarRegister
                .lookup("MixingProxyRegistrarService");

        System.out.println("Started all RMI client instances");
    }

    @Override
    public void addTokensToMixingProxy(LocalDate date, ArrayList<byte[]> tokens) throws Exception {
        mixingProxyRegistrarService.addTokens(date, tokens);
    }

    @Override
    public byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws Exception {
        return registrarController.getFacilityPseudonym(facilityIdentifier, date);
    }

    @Override
    public void addUnacknowledgedTokens(ArrayList<byte[]> unacknowledgedTokens) throws Exception {
        registrarController.addUnacknowledgedTokens(unacknowledgedTokens);
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
