package Connection;

import Common.Messages.CapsuleVerification;
import Common.Messages.TokenUpdate;
import Common.Objects.Capsule;
import Common.RMIInterfaces.MixingProxy.MixingProxyUserService;
import Common.RMIInterfaces.Registrar.RegistrarUserService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;

public class ConnectionControllerImpl implements ConnectionController{
    private static final int registrarUserRMIClientPort = 3333;
    private static final int mixingProxyUserRMIClientPort = 4444;
    private static RegistrarUserService registrarUserService;
    private static MixingProxyUserService mixingProxyUserService;

    public ConnectionControllerImpl() {
    }

    @Override
    public void startClientConnections() throws RemoteException, NotBoundException {
        //Connect to the user Registrar service
        Registry registrarUserRegister = LocateRegistry.getRegistry("localhost", registrarUserRMIClientPort);
        registrarUserService = (RegistrarUserService) registrarUserRegister
                .lookup("RegistrarUserService");

        //Connect to the user Mixing proxy service
        Registry mixingProxyUserRegister = LocateRegistry.getRegistry("localhost", mixingProxyUserRMIClientPort);
        mixingProxyUserService = (MixingProxyUserService) mixingProxyUserRegister
                .lookup("MixingProxyUserService");
    }

    @Override
    public void registerUSer(String userIdentifier) throws Exception {
        registrarUserService.registerUser(userIdentifier);
    }

    @Override
    public TokenUpdate getTodaysTokens(String userIdentifier) throws Exception {
        LocalDate today = LocalDate.now();
        return registrarUserService.getTokens(userIdentifier, today);
    }

    @Override
    public CapsuleVerification registerCapsule(Capsule capsule) throws Exception {
        return mixingProxyUserService.registerCapsule(capsule);
    }

}
