package Connection;

import Common.Messages.CapsuleVerification;
import Common.Messages.TokenUpdate;
import Common.Objects.CapsuleLog;
import Common.RMIInterfaces.MatchingService.MatchingServicePractitioner;
import Common.RMIInterfaces.MatchingService.MatchingServiceUser;
import Common.RMIInterfaces.MixingProxy.MixingProxyUserService;
import Common.RMIInterfaces.Registrar.RegistrarUserService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.ArrayList;

public class ConnectionControllerImpl implements ConnectionController{
    private static final int registrarUserRMIClientPort = 3333;
    private static final int mixingProxyUserRMIClientPort = 4444;
    private static final int MatchingServiceUserRMIClientPort = 8888;
    private static RegistrarUserService registrarUserService;
    private static MixingProxyUserService mixingProxyUserService;
    private static MatchingServiceUser matchingServiceUser;

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

        //Connect to the matching service
        Registry matchingServiceUserRegistry = LocateRegistry.getRegistry("localhost", MatchingServiceUserRMIClientPort);
        matchingServiceUser = (MatchingServiceUser) matchingServiceUserRegistry
                .lookup("MatchingServiceUser");
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
    public CapsuleVerification registerCapsule(CapsuleLog capsuleLog) throws Exception {
        return mixingProxyUserService.registerCapsule(capsuleLog);
    }

    @Override
    public ArrayList<CapsuleLog> getInfectedCapsules() throws Exception {
        return matchingServiceUser.getInfectedCapsules();
    }

    @Override
    public void acknowledgeTokens(ArrayList<byte[]> acknowledgeTokens) throws Exception {
        mixingProxyUserService.acknowledgeTokens(acknowledgeTokens);
    }

}
