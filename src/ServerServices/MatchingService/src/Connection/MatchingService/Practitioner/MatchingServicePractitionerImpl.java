package Connection.MatchingService.Practitioner;

import Common.Messages.InfectedUserMessage;
import Common.RMIInterfaces.MatchingService.MatchingServicePractitioner;
import Connection.ConnectionController;

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
