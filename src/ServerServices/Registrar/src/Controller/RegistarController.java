package Controller;

import Common.Messages.TokenUpdate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

public interface RegistarController {
    //Catering facility enrollment
    void registerCateringFacility(int facilityIdentifier) throws Exception;
    HashMap<Calendar, byte[]> getPseudomyms(int facilityIdentifier, int year, int monthIndex) throws Exception;

    //User enrollment
    void registerUser(int userIdentifier) throws SQLException;
    TokenUpdate getTokens(int userIdentifier, Calendar date) throws Exception;
}
