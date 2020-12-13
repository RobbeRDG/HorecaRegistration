package Connection.MatchingService.Practitioner;

import Messages.InfectedUserMessage;
import Connection.ConnectionController;
import RMIInterfaces.MatchingService.MatchingServicePractitioner;

public class MatchingServicePractitionerImpl implements MatchingServicePractitioner {
    private ConnectionController connectionController;

    public MatchingServicePractitionerImpl(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    @Override
    public void addInfectedUser(InfectedUserMessage infectedUserMessage) throws Exception {
        connectionController.addInfectedUser(infectedUserMessage);
    }

}
