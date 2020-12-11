package Common.RMIInterfaces.MixingProxy;

import Common.Messages.CapsuleVerification;
import Common.Objects.CapsuleLog;

import java.rmi.Remote;

public interface MixingProxyUserService extends Remote {
    public CapsuleVerification registerCapsule(CapsuleLog capsuleLog) throws Exception;
}
