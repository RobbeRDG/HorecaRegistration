package Connection.Registrar.Facility;

import Common.Messages.PseudonymUpdate;
import Common.RMIInterfaces.Registrar.RegistrarFacilityService;
import Connection.ConnectionController;

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
