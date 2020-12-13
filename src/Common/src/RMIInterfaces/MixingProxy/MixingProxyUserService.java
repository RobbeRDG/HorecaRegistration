package RMIInterfaces.MixingProxy;

import Messages.CapsuleVerification;
import Objects.CapsuleLog;

import java.rmi.Remote;
import java.util.ArrayList;

public interface MixingProxyUserService extends Remote {
    public CapsuleVerification registerCapsule(CapsuleLog capsuleLog) throws Exception;

    void acknowledgeTokens(ArrayList<byte[]> acknowledgeTokens) throws Exception;
}
