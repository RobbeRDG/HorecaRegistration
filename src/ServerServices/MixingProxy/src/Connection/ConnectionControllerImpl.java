package Connection;

import Connection.MixingProxy.MixingProxyUserService;
import Connection.MixingProxy.MixingProxyUserServiceImpl;
import Controller.MixingProxyController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ConnectionControllerImpl implements ConnectionController {
    private static MixingProxyController mixingProxyController;
    private static MixingProxyUserService mixingProxyUserServer;
    private static final int mixingProxyUserServicePort = 4444;

    public ConnectionControllerImpl(MixingProxyController mixingProxyController) {
        this.mixingProxyController = mixingProxyController;
        mixingProxyUserServer = new MixingProxyUserServiceImpl(this);
    }

    @Override
    public void startServerConnections() throws RemoteException {
        //Start the mixing proxy user server
        MixingProxyUserService mixingProxyUserServiceStub = (MixingProxyUserService) UnicastRemoteObject
                .exportObject((MixingProxyUserService) mixingProxyUserServer, 0);

        Registry mixingProxyUserServiceRegistry = LocateRegistry.createRegistry(mixingProxyUserServicePort);
        mixingProxyUserServiceRegistry.rebind("MixingProxyUserService", mixingProxyUserServiceStub);

        System.out.println("Started all RMI server instances");
    }

    public void startClientConnections() {

    }
}
