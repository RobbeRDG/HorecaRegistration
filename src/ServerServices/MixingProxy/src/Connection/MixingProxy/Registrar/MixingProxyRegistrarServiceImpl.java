package Connection.MixingProxy.Registrar;

import Common.RMIInterfaces.MixingProxy.MixingProxyRegistrarService;
import Connection.ConnectionController;

import java.time.LocalDate;
import java.util.ArrayList;

public class MixingProxyRegistrarServiceImpl implements MixingProxyRegistrarService {
    private static ConnectionController connectionController;

    public MixingProxyRegistrarServiceImpl(ConnectionController connectionController) {
        if (this.connectionController == null) this.connectionController = connectionController;
    }

    @Override
    public void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception {
        connectionController.addTokens(date, tokens);
    }
}
