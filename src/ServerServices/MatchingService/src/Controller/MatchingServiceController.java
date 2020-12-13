package Controller;

import Messages.InfectedUserMessage;
import Objects.CapsuleLog;

import java.security.PrivateKey;
import java.util.ArrayList;

public interface MatchingServiceController {
    void addInfectedUser(InfectedUserMessage infectedUserMessage) throws Exception;

    void addCapsules(ArrayList<CapsuleLog> capsules) throws Exception;

    void submitAcknowledgements(ArrayList<byte[]> acknowledgementTokens) throws Exception;

    ArrayList<CapsuleLog> getInfectedCapsules() throws Exception;
    void sendUnacknowledgedTokens();
    void deleteExpiredCapsules();
    void refreshPrimaryStage();
}
