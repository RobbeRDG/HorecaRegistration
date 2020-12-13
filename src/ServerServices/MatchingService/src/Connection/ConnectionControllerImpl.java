package Connection;

import RMIInterfaces.MatchingService.MatchingServiceMixingProxy;
import Connection.MatchingService.MixingProxy.MatchingServiceMixingProxyImpl;
import RMIInterfaces.MatchingService.MatchingServicePractitioner;
import Connection.MatchingService.Practitioner.MatchingServicePractitionerImpl;
import RMIInterfaces.MatchingService.MatchingServiceUser;
import Connection.MatchingService.User.MatchingServiceUserImpl;
import Controller.MatchingServiceController;
import Messages.InfectedUserMessage;
import Objects.CapsuleLog;
import RMIInterfaces.Registrar.RegistrarMatchingService;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.ArrayList;

public class ConnectionControllerImpl implements ConnectionController{
    private static MatchingServiceController matchingServiceController;
    private static MatchingServiceMixingProxy matchingServiceMixingProxyServer;
    private static MatchingServicePractitioner matchingServicePractitionerServer;
    private static MatchingServiceUser matchingServiceUserServer;
    private static RegistrarMatchingService registrarMatchingService;
    private static final int matchingServiceMixingProxyRMIServerPort = 6666;
    private static final int matchingServicePractitionerRMIServerPort = 7777;
    private static final int matchingServiceUserRMIServerPort = 8888;
    private static final int registrarMatchingServiceRMIClientPort = 9999;

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
        matchingServiceMixingProxyRegistry.rebind("MatchingServiceMixingProxy", matchingServiceMixingProxyStub);

        //Start the matching service practitioner server
        MatchingServicePractitioner matchingServicePractitionerStub = (MatchingServicePractitioner) UnicastRemoteObject
                .exportObject((MatchingServicePractitioner) matchingServicePractitionerServer, 0);

        Registry matchingServicePractitionerRegistry = LocateRegistry.createRegistry(matchingServicePractitionerRMIServerPort);
        matchingServicePractitionerRegistry.rebind("MatchingServicePractitioner", matchingServicePractitionerStub);

        //Start the matching service user server
        MatchingServiceUser matchingServiceUserStub = (MatchingServiceUser) UnicastRemoteObject
                .exportObject((MatchingServiceUser) matchingServiceUserServer, 0);

        Registry matchingServiceUserRegistry = LocateRegistry.createRegistry(matchingServiceUserRMIServerPort);
        matchingServiceUserRegistry.rebind("MatchingServiceUser", matchingServiceUserStub);

        System.out.println("Started all RMI server instances");
    }


    public void startClientConnections() throws RemoteException, NotBoundException {
        //Connect to the Registrar Server
        Registry registrarMatchingServiceRegistry = LocateRegistry.getRegistry("localhost", registrarMatchingServiceRMIClientPort);
        registrarMatchingService = (RegistrarMatchingService) registrarMatchingServiceRegistry
                .lookup("RegistrarMatchingService");

        System.out.println("Started all RMI client instances");
    }

    @Override
    public void addInfectedUser(InfectedUserMessage infectedUserMessage) throws Exception {
        matchingServiceController.addInfectedUser(infectedUserMessage);
    }

    public byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws Exception {
        return registrarMatchingService.getFacilityPseudonym(facilityIdentifier, date);
    }

    @Override
    public void addCapsules(ArrayList<CapsuleLog> capsules) throws Exception {
        matchingServiceController.addCapsules(capsules);
    }

    @Override
    public void submitAcknowledgements(ArrayList<byte[]> acknowledgementTokens) throws Exception {
        matchingServiceController.submitAcknowledgements(acknowledgementTokens);
    }

    @Override
    public ArrayList<CapsuleLog> getInfectedCapsules() throws Exception {
        return matchingServiceController.getInfectedCapsules();
    }

    @Override
    public void addUnacknowledgedTokens(ArrayList<byte[]> unacknowledgedTokens) throws Exception {
        registrarMatchingService.addUnacknowledgedTokens(unacknowledgedTokens);
    }
}
