package Connection.MatchingService.MixingProxy;

import Connection.ConnectionController;
import Controller.MatchingServiceController;

public class MatchingServiceMixingProxyImpl implements MatchingServiceMixingProxy {
    private ConnectionController connectionController;

    public MatchingServiceMixingProxyImpl(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }
}
