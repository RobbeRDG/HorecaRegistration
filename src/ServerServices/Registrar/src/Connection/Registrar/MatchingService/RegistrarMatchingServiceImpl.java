package Connection.Registrar.MatchingService;

import Connection.ConnectionController;
import RMIInterfaces.Registrar.RegistrarMatchingService;

import java.time.LocalDate;
import java.util.ArrayList;

public class RegistrarMatchingServiceImpl implements RegistrarMatchingService {
    private static ConnectionController connectionController;

    public RegistrarMatchingServiceImpl(ConnectionController connectionController) {
        if (this.connectionController == null) this.connectionController = connectionController;
    }

    @Override
    public byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws Exception {
        return connectionController.getFacilityPseudonym(facilityIdentifier, date);
    }

    @Override
    public void addUnacknowledgedTokens(ArrayList<byte[]> unacknowledgedTokens) throws Exception {
        connectionController.addUnacknowledgedTokens(unacknowledgedTokens);
    }
}
