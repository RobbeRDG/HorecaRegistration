package Connection.Registrar.Facility;

import Messages.PseudonymUpdate;
import Connection.ConnectionController;
import RMIInterfaces.Registrar.RegistrarFacilityService;

public class RegistrarFacilityServiceImpl implements RegistrarFacilityService {
    private static ConnectionController connectionController;

    public RegistrarFacilityServiceImpl(ConnectionController connectionController) {
        if (this.connectionController == null) this.connectionController = connectionController;
    }

    ///////////////////////////////////////////////////////////////////
    ///         CATERING FACILITY ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    @Override
    public void registerCateringFacility(String facilityIdentifier) throws Exception {
        connectionController.registerCateringFacility(facilityIdentifier);
    }

    @Override
    public PseudonymUpdate getPseudonyms(String facilityIdentifier, int year, int monthIndex) throws Exception {
        return connectionController.getPseudomyms(facilityIdentifier, year, monthIndex);
    }

}
