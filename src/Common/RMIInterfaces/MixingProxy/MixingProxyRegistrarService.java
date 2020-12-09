package Common.RMIInterfaces.MixingProxy;


import Common.Messages.TokenUpdate;

import java.rmi.Remote;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface MixingProxyRegistrarService extends Remote {
    void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception;
}