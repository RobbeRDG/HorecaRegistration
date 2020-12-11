package Controller;

import Common.Messages.InfectedUserMessage;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public interface MatchingServiceController {
    void addInfectedUser(InfectedUserMessage infectedUserMessage) throws Exception;
}
