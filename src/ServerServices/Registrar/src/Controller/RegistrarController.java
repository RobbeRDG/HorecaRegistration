package Controller;

import Common.Exceptions.AlreadyRegisteredException;
import Common.Messages.PseudonymUpdate;
import Common.Messages.TokenUpdate;
import Common.Objects.Token;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public interface RegistrarController {
    //Catering facility enrollment
    void registerCateringFacility(String facilityIdentifier) throws Exception;
    PseudonymUpdate getPseudomyms(String facilityIdentifier, int year, int monthIndex) throws Exception;

    //User enrollment
    void registerUser(String userIdentifier) throws SQLException, AlreadyRegisteredException;
    TokenUpdate getTokens(String userIdentifier, LocalDate date) throws Exception;

    byte[] getFacilityPseudonym(String facilityIdentifier, LocalDate date) throws SQLException;

    void addUnacknowledgedTokens(ArrayList<byte[]> unacknowledgedTokens) throws Exception;
}
