package Connection.MatchingService.User;

import Objects.CapsuleLog;
import Connection.ConnectionController;
import RMIInterfaces.MatchingService.MatchingServiceUser;

import java.util.ArrayList;

public class MatchingServiceUserImpl implements MatchingServiceUser {
    private ConnectionController connectionController;

    public MatchingServiceUserImpl(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    @Override
    public ArrayList<CapsuleLog> getInfectedCapsules() throws Exception {
        return connectionController.getInfectedCapsules();
    }
}
