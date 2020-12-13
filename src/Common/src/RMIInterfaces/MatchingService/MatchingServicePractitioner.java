package RMIInterfaces.MatchingService;

import Messages.InfectedUserMessage;

import java.rmi.Remote;

public interface MatchingServicePractitioner extends Remote {
    void addInfectedUser(InfectedUserMessage infectedUser) throws Exception;
}
