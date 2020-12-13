package Connection.MixingProxy.User;

import Common.Messages.CapsuleVerification;
import Common.Objects.CapsuleLog;
import Common.RMIInterfaces.MixingProxy.MixingProxyUserService;
import Connection.ConnectionController;

import java.util.ArrayList;

public class MixingProxyUserServiceImpl implements MixingProxyUserService {
    private static ConnectionController connectionController;

    public MixingProxyUserServiceImpl(ConnectionController connectionController) {
        if (this.connectionController == null) this.connectionController = connectionController;
    }

    @Override
    public CapsuleVerification registerCapsule(CapsuleLog capsuleLog) throws Exception {
        return connectionController.registerToken(capsuleLog);
    }

    @Override
    public void acknowledgeTokens(ArrayList<byte[]> acknowledgeTokens) throws Exception {
        connectionController.acknowledgeTokens(acknowledgeTokens);
    }
}
