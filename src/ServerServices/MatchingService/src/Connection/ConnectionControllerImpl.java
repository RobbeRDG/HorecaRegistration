package Connection;

import Common.Messages.InfectedUserMessage;
import Common.RMIInterfaces.Registrar.RegistrarMatchingService;
import Connection.MatchingService.MixingProxy.MatchingServiceMixingProxy;
import Connection.MatchingService.MixingProxy.MatchingServiceMixingProxyImpl;
import Connection.MatchingService.Practitioner.MatchingServicePractitioner;
import Connection.MatchingService.Practitioner.MatchingServicePractitionerImpl;
import Connection.MatchingService.User.MatchingServiceUser;
import Connection.MatchingService.User.MatchingServiceUserImpl;
import Controller.MatchingServiceController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;

public class ConnectionControllerImpl implements ConnectionController{
    private static MatchingServiceController matchingServiceController;
    private static MatchingServiceMixingProxy matchingServiceMixingProxyServer;
    private static MatchingServicePractitioner matchingServicePractitionerServer;
    private static MatchingServiceUser matchingServiceUserServer;
    private static RegistrarMatchingService registrarMatchingService;
    private static final int matchingServiceMixingProxyRMIServerPort = 6666;
    private static final int matchingServicePractitionerRMIServerPort = 7777;
    private static final int matchingServiceUserRMIServerPort = 8888;

    public ConnectionControllerImpl(MatchingServiceController matchingServiceController) {
        this.matchingServiceController = matchingServiceController;
        matchingServiceMixingProxyServer = new MatchingServiceMixingProxyImpl(this);
        matchingServicePractitionerServer = new MatchingServicePractitionerImpl(this);
        matchingServiceUserServer = new MatchingServiceUserImpl(this);
    }

    @Override
    public void startServerConnections() throws RemoteException {
        //Start the matching service mixing proxy server
        MatchingServiceMixingProxy matchingServiceMixingProxyStub = (MatchingServiceMixingProxy) UnicastRemoteObject
                .exportObject((MatchingServiceMixingProxy) matchingServiceMixingProxyServer, 0);

        Registry matchingServiceMixingProxyRegistry = LocateRegistry.createRegistry(matchingServiceMixingProxyRMIServerPort);
        matchingServiceMixingProxyRegistry.rebind("MixingProxyUserService", matchingServiceMixingProxyStub);

        //Start the matching service practitioner server
        MatchingServicePractitioner matchingServicePractitionerStub = (MatchingServicePractitioner) UnicastRemoteObject
                .exportObject((MatchingServicePractitioner) matchingServicePractitionerServer, 0);

        Registry matchingServicePractitionerRegistry = LocateRegistry.createRegistry(matchingServicePractitionerRMIServerPort);
        matchingServicePractitionerRegistry.rebind("MixingProxyRegistrarService", matchingServicePractitionerStub);

        //Start the matching service user server
        MatchingServiceUser matchingServiceUserStub = (MatchingServiceUser) UnicastRemoteObject
                .exportObject((MatchingServiceUser) matchingServiceUserServer, 0);

        Registry matchingServiceUserRegistry = LocateRegistry.createRegistry(matchingServiceUserRMIServerPort);
        matchingServiceUserRegistry.rebind("MixingProxyRegistrarService", matchingServiceUserStub);

        System.out.println("Started all RMI server instances");
    }


    public void startClientConnections() {

    }

    @Override
    public void addInfectedUser(InfectedUserMessage infectedUserMessage) throws Exception {
        matchingServiceController.addInfectedUser(infectedUserMessage);
    }

    public byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws SQLException {
        return registrarMatchingService.getFacilityPseudonym(facilityIdentifier, date);
    }
}
