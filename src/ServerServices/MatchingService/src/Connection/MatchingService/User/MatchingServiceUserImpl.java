package Connection.MatchingService.User;

import Common.Objects.CapsuleLog;
import Common.RMIInterfaces.MatchingService.MatchingServiceUser;
import Connection.ConnectionController;

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
