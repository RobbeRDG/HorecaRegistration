package Connection;

import Common.Messages.InfectedUserMessage;
import Common.RMIInterfaces.MatchingService.MatchingServicePractitioner;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ConnectionControllerImpl implements ConnectionController{
    private static final int MatchingServiceRMIClientPort = 7777;
    private static MatchingServicePractitioner matchingServicePractitioner;

    public ConnectionControllerImpl () {
    }


    @Override
    public void startClientConnections() throws RemoteException, NotBoundException {
        //Connect to the facility service
        Registry matchingServicePractitionerRegistry = LocateRegistry.getRegistry("localhost", MatchingServiceRMIClientPort);
        matchingServicePractitioner = (MatchingServicePractitioner) matchingServicePractitionerRegistry
                .lookup("MatchingServicePractitioner");
    }

    @Override
    public void addInfectedUser(InfectedUserMessage message) throws Exception {
        matchingServicePractitioner.addInfectedUser(message);
    }


}
