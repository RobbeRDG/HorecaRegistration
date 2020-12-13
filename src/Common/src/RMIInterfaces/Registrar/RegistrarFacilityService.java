package RMIInterfaces.Registrar;

import Messages.PseudonymUpdate;

import java.rmi.Remote;

public interface RegistrarFacilityService extends Remote {
    void registerCateringFacility(String facilityIdentifier) throws Exception;
    PseudonymUpdate getPseudonyms(String facilityIdentifier, int year, int monthIndex) throws Exception;
}
