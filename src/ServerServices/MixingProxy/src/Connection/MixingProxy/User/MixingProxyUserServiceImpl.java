package Connection.MixingProxy.User;

import Messages.CapsuleVerification;
import Objects.CapsuleLog;
import Connection.ConnectionController;
import RMIInterfaces.MixingProxy.MixingProxyUserService;

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
