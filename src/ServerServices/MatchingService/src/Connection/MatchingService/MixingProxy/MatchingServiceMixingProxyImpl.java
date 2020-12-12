package Connection.MatchingService.MixingProxy;

import Common.Objects.CapsuleLog;
import Common.RMIInterfaces.MatchingService.MatchingServiceMixingProxy;
import Connection.ConnectionController;

import java.util.ArrayList;

public class MatchingServiceMixingProxyImpl implements MatchingServiceMixingProxy {
    private ConnectionController connectionController;

    public MatchingServiceMixingProxyImpl(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    @Override
    public void submitCapsules(ArrayList<CapsuleLog> capsules) throws Exception {
        connectionController.addCapsules(capsules);
    }

    @Override
    public void submitAcknowledgements(ArrayList<byte[]> acknowledgementTokens) throws Exception {
        connectionController.submitAcknowledgements(acknowledgementTokens);
    }
}
