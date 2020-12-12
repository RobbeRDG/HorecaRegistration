package Common.RMIInterfaces.Registrar;

import java.rmi.Remote;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface RegistrarMatchingService extends Remote {
    byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws SQLException, Exception;
    void addUnacknowledgedTokens(ArrayList<byte[]> unacknowledgedTokens) throws Exception;
}
