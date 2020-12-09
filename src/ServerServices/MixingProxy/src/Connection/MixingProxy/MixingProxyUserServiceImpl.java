package Connection.MixingProxy;

import Connection.ConnectionController;

public class MixingProxyUserServiceImpl implements MixingProxyUserService{
    private static ConnectionController connectionController;

    public MixingProxyUserServiceImpl(ConnectionController connectionController) {
        if (this.connectionController == null) this.connectionController = connectionController;
    }
}
