package RMIInterfaces.MixingProxy;


import java.rmi.Remote;
import java.time.LocalDate;
import java.util.ArrayList;

public interface MixingProxyRegistrarService extends Remote {
    void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception;
}