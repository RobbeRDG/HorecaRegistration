package RMIInterfaces.MatchingService;

import Objects.CapsuleLog;

import java.rmi.Remote;
import java.util.ArrayList;

public interface MatchingServiceUser extends Remote {
    ArrayList<CapsuleLog> getInfectedCapsules() throws Exception;
}
