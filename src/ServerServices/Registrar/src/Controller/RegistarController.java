package Controller;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public interface RegistarController {
    //Catering facility enrollment
    void registerCateringFacility(String facilityIdentifier) throws Exception;
    HashMap<LocalDate, byte[]> getPseudomyms(String facilityIdentifier) throws Exception;

    //User enrollment
    void registerUser(String userIdentifier);
    byte[] getToken(String userIdentifier);
}
