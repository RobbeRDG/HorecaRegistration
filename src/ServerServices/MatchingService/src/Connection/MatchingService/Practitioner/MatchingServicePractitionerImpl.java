package Connection.MatchingService.Practitioner;

import Common.Messages.InfectedUserMessage;
import Common.Objects.InfectedUser;
import Connection.ConnectionController;

public class MatchingServicePractitionerImpl implements MatchingServicePractitioner{
    private ConnectionController connectionController;

    public MatchingServicePractitionerImpl(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    @Override
    public void addInfectedUser(InfectedUserMessage infectedUserMessage) {
        connectionController.addInfectedUser(infectedUserMessage);
    }

}
