package Controller;

import Common.Messages.InfectedUserMessage;
import Common.Objects.CapsuleLog;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface MatchingServiceController {
    void addInfectedUser(InfectedUserMessage infectedUserMessage) throws Exception;

    void addCapsules(ArrayList<CapsuleLog> capsules) throws Exception;

    void submitAcknowledgements(ArrayList<byte[]> acknowledgementTokens) throws Exception;

    ArrayList<CapsuleLog> getInfectedCapsules() throws Exception;
    void sendUnacknowledgedTokens();
    void deleteExpiredCapsules();
}
