package Connection;

import Controller.RegistarController;

import java.rmi.AlreadyBoundException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.HashMap;

public class ConnectionImpl implements Connection{
    private static RegistarController registarController;

    public ConnectionImpl(RegistarController registarController) {
        if (this.registarController == null) this.registarController = registarController;
    }

    public void startServer() throws RemoteException, AlreadyBoundException {
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry(2222);
        registry.bind("RegistrarService", new ConnectionImpl(registarController));

        System.out.println("RMI Connection Ready");
    }



    ///////////////////////////////////////////////////////////////////
    ///         CATERING FACILITY ENROLLMENT
    ///////////////////////////////////////////////////////////////////
    @Override
    public void registerCateringFacility(String facilityIdentifier) throws Exception {
        registarController.registerCateringFacility(facilityIdentifier);
    }

    @Override
    public HashMap<LocalDate, byte[]> getPseudonyms(String facilityIdentifier) throws RemoteException {
        return registarController.getPseudomyms(facilityIdentifier);
    }


    ///////////////////////////////////////////////////////////////////
    ///         USER ENROLLMENT
    ///////////////////////////////////////////////////////////////////

}
