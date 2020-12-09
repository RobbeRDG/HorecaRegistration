package Common.RMIInterfaces.MixingProxy;

import Common.Messages.CapsuleVerification;
import Common.Objects.Capsule;

import java.rmi.Remote;

public interface MixingProxyUserService extends Remote {
    public CapsuleVerification registerCapsule(Capsule capsule) throws Exception;
}
