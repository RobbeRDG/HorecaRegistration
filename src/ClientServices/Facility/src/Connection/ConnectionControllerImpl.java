package Connection;

import Common.Messages.PseudonymUpdate;
import Common.RMIInterfaces.Registrar.RegistrarFacilityService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ConnectionControllerImpl implements ConnectionController{
    private static final int registrarFacilityServerPort = 2222;
    private static RegistrarFacilityService registrarFacilityService;

    public ConnectionControllerImpl () {
    }

    @Override
    public void connectToServices() throws RemoteException, NotBoundException {
        //Connect to the facility service
        Registry registrarFacilityRegister = LocateRegistry.getRegistry("localhost", registrarFacilityServerPort);
        registrarFacilityService = (RegistrarFacilityService) registrarFacilityRegister
                .lookup("RegistrarFacilityService");
    }

    @Override
    public PseudonymUpdate getPseudonyms(String facilityIdentifier, int year, int monthIndex) throws Exception {
        return registrarFacilityService.getPseudonyms(facilityIdentifier, year, monthIndex);
    }

    @Override
    public void registerCateringFacility(String facilityIdentifier) throws Exception {
        registrarFacilityService.registerCateringFacility(facilityIdentifier);
    }
}
