package Controller;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public interface RegistarController {
    //Catering facility enrollment
    void registerCateringFacility(int facilityIdentifier) throws Exception;
    HashMap<Calendar, byte[]> getPseudomyms(int facilityIdentifier, int year, int monthIndex) throws Exception;

    //User enrollment
    void registerUser(int userIdentifier) throws SQLException;
    byte[] getTokens(int userIdentifier, Calendar date);
}
