package Connection.MatchingService.User;

import Connection.ConnectionController;
import Controller.MatchingServiceController;

public class MatchingServiceUserImpl implements MatchingServiceUser {
    private ConnectionController connectionController;

    public MatchingServiceUserImpl(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }
}
