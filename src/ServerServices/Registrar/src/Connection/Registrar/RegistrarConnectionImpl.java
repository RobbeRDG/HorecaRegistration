package Connection.Registrar;

import Common.Messages.PseudonymUpdate;
import Common.Messages.TokenUpdate;
import Controller.RegistarController;

import java.util.Calendar;

public class RegistrarConnectionImpl implements RegistrarConnection {
    private static RegistarController registarController;

    public RegistrarConnectionImpl(RegistarController registarController) {
        if (this.registarController == null) this.registarController = registarController;
    }

    ///////////////////////////////////////////////////////////////////
    ///         CATERING FACILITY ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    @Override
    public void registerCateringFacility(String facilityIdentifier) throws Exception {
        registarController.registerCateringFacility(facilityIdentifier);
    }

    @Override
    public PseudonymUpdate getPseudonyms(String facilityIdentifier, int year, int monthIndex) throws Exception {
        return registarController.getPseudomyms(facilityIdentifier, year, monthIndex);
    }


    ///////////////////////////////////////////////////////////////////
    ///         USER ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    @Override
    public void registerUser(String userIdentifier) throws Exception {
        registarController.registerUser(userIdentifier);
    }

    @Override
    public TokenUpdate getTokens(String userIdentifier, Calendar date) throws Exception {
        return registarController.getTokens(userIdentifier, date);
    }
}
