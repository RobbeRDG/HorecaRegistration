package Connection.Registrar.MatchingService;

import Common.RMIInterfaces.Registrar.RegistrarMatchingService;
import Connection.ConnectionController;

import java.sql.SQLException;
import java.time.LocalDate;

public class RegistrarMatchingServiceImpl implements RegistrarMatchingService {
    private static ConnectionController connectionController;

    public RegistrarMatchingServiceImpl(ConnectionController registarController) {
        if (this.connectionController == null) this.connectionController = registarController;
    }

    @Override
    public byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws SQLException {
        return connectionController.getFacilityPseudonym(facilityIdentifier, date);
    }
}
