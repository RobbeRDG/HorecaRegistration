package Common.RMIInterfaces.MatchingService;

import Common.Messages.InfectedUserMessage;
import Common.Objects.InfectedUser;

import java.rmi.Remote;

public interface MatchingServicePractitioner extends Remote {
    void addInfectedUser(InfectedUserMessage infectedUser) throws Exception;
}