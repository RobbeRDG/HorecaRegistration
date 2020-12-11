package Common.RMIInterfaces.Registrar;

import java.sql.SQLException;
import java.time.LocalDate;

public interface RegistrarMatchingService {
    byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws SQLException;
}
