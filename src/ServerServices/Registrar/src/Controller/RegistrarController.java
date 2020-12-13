package Controller;

import Exceptions.AlreadyRegisteredException;
import Messages.PseudonymUpdate;
import Messages.TokenUpdate;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface RegistrarController {
    public void refreshPrimaryStage();

    //Catering facility enrollment
    void registerCateringFacility(String facilityIdentifier) throws Exception;
    PseudonymUpdate getPseudomyms(String facilityIdentifier, int year, int monthIndex) throws Exception;

    //User enrollment
    void registerUser(String userIdentifier) throws SQLException, AlreadyRegisteredException;
    TokenUpdate getTokens(String userIdentifier, LocalDate date) throws Exception;

    //Matching service
    byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws Exception;
    void addUnacknowledgedTokens(ArrayList<byte[]> unacknowledgedTokens) throws Exception;
}
