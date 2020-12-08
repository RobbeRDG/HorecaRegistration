package Connection.Registrar.User;

import Common.Messages.TokenUpdate;

import java.rmi.Remote;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;

public interface RegistrarUserService extends Remote {
    void registerUser(String userIdentifier) throws Exception;
    TokenUpdate getTokens(String userIdentifier, LocalDate date) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, Exception;
}
