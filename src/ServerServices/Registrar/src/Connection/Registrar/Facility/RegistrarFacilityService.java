package Connection.Registrar.Facility;

import Common.Messages.PseudonymUpdate;
import Common.Messages.TokenUpdate;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;

public interface RegistrarFacilityService extends Remote {
    void registerCateringFacility(String facilityIdentifier) throws Exception;
    PseudonymUpdate getPseudonyms(String facilityIdentifier, int year, int monthIndex) throws Exception;
}
