package Common.RMIInterfaces.Registrar;

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

public interface RegistrarUserService extends Remote {
    void registerUser(String userIdentifier) throws Exception;
    TokenUpdate getTokens(String userIdentifier, LocalDate date) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, Exception;
}
