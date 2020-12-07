package Controller;

import Common.Exceptions.AlreadyRegisteredException;
import Common.Messages.PseudonymUpdate;
import Common.Messages.TokenUpdate;

import java.sql.SQLException;
import java.util.Calendar;

public interface RegistarController {
    //Catering facility enrollment
    void registerCateringFacility(String facilityIdentifier) throws Exception;
    PseudonymUpdate getPseudomyms(String facilityIdentifier, int year, int monthIndex) throws Exception;

    //User enrollment
    void registerUser(String userIdentifier) throws SQLException, AlreadyRegisteredException;
    TokenUpdate getTokens(String userIdentifier, Calendar date) throws Exception;
}
