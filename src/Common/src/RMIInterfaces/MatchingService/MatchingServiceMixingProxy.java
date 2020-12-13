package RMIInterfaces.MatchingService;

import Objects.CapsuleLog;

import java.rmi.Remote;
import java.util.ArrayList;

public interface MatchingServiceMixingProxy extends Remote {
    void submitCapsules(ArrayList<CapsuleLog> capsules) throws Exception;
    void submitAcknowledgements(ArrayList<byte[]> acknowledgementTokens) throws Exception;
}
