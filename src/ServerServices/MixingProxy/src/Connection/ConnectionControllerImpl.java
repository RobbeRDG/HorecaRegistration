package Connection;

import Common.Messages.CapsuleVerification;
import Common.Objects.CapsuleLog;
import Common.RMIInterfaces.MatchingService.MatchingServiceMixingProxy;
import Common.RMIInterfaces.MixingProxy.MixingProxyRegistrarService;
import Connection.MixingProxy.Registrar.MixingProxyRegistrarServiceImpl;
import Common.RMIInterfaces.MixingProxy.MixingProxyUserService;
import Connection.MixingProxy.User.MixingProxyUserServiceImpl;
import Controller.MixingProxyController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.ArrayList;

public class ConnectionControllerImpl implements ConnectionController {
    private static MixingProxyController mixingProxyController;
    private static MixingProxyUserService mixingProxyUserServer;
    private static MixingProxyRegistrarService mixingProxyRegistrarServer;
    private static MatchingServiceMixingProxy matchingServiceMixingProxy;
    private static final int mixingProxyUserRMIServerPort = 4444;
    private static final int mixingProxyRegistrarRMIServerPort = 5555;
    private static final int matchingServiceMixingProxyRMIClientPort = 6666;

    public ConnectionControllerImpl(MixingProxyController mixingProxyController) {
        this.mixingProxyController = mixingProxyController;
        mixingProxyUserServer = new MixingProxyUserServiceImpl(this);
        mixingProxyRegistrarServer = new MixingProxyRegistrarServiceImpl(this);
    }

    @Override
    public void startServerConnections() throws RemoteException {
        //Start the mixing proxy user server
        MixingProxyUserService mixingProxyUserServiceStub = (MixingProxyUserService) UnicastRemoteObject
                .exportObject((MixingProxyUserService) mixingProxyUserServer, 0);

        Registry mixingProxyUserServiceRegistry = LocateRegistry.createRegistry(mixingProxyUserRMIServerPort);
        mixingProxyUserServiceRegistry.rebind("MixingProxyUserService", mixingProxyUserServiceStub);

        //Start the mixing proxy registrar server
        MixingProxyRegistrarService mixingProxyRegistrarServiceStub = (MixingProxyRegistrarService) UnicastRemoteObject
                .exportObject((MixingProxyRegistrarService) mixingProxyRegistrarServer, 0);

        Registry mixingProxyRegistrarServiceRegistry = LocateRegistry.createRegistry(mixingProxyRegistrarRMIServerPort);
        mixingProxyRegistrarServiceRegistry.rebind("MixingProxyRegistrarService", mixingProxyRegistrarServiceStub);


        System.out.println("Started all RMI server instances");
    }

    @Override
    public CapsuleVerification registerToken(CapsuleLog capsuleLog) throws Exception {
        return mixingProxyController.registerToken(capsuleLog);
    }

    @Override
    public void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception {
        mixingProxyController.addTokens(date, tokens);
    }

    @Override
    public void startClientConnections() throws RemoteException, NotBoundException {
        //Connect to the matching service
        Registry matchingServiceMixingProxyRegistry = LocateRegistry.getRegistry("localhost", matchingServiceMixingProxyRMIClientPort);
        matchingServiceMixingProxy = (MatchingServiceMixingProxy) matchingServiceMixingProxyRegistry
                .lookup("MatchingServiceMixingProxy");

        System.out.println("Started all RMI client instances");
    }
}
