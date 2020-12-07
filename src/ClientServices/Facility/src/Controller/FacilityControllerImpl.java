package Controller;

import Common.Exceptions.NotRegisteredException;
import Common.Messages.PseudonymUpdate;
import Connection.Registrar.RegistrarConnection;
import QRGenerator.QRGenerator;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class FacilityControllerImpl {
    private static final QRGenerator qrCodeGenerator = new QRGenerator();
    private RegistrarConnection registrarConnection;
    private static final int registrarPort = 2222;
    private static String facilityIdentifier;
    private static final Scanner sc = new Scanner(System.in);
    private static HashMap<LocalDate, byte[]> dailyPseudonyms;
    private static HashMap<LocalDate, byte[]> dailyRandoms;
    private static byte[] QRCodeForToday;
    private static final int randomLength = 128;

    public FacilityControllerImpl() {
        dailyPseudonyms = new HashMap<>();
        dailyRandoms = new HashMap<>();
    }

    public static void main(String[] args) {
        FacilityControllerImpl facilityController = new FacilityControllerImpl();
        facilityController.start();
        facilityController.stop();
    }

    private void stop() {
        System.exit(0);
    }

    private void handleException(Exception e) {
        System.out.println(e.getMessage());
    }

    private void start() {
        try {
            System.out.println("Starting Facility controller");

            //Ask for the facility identifier
            getFacilityIdentifier();

            //Connect to the registrar server
            connectToRegistrar();

            //Choose between registering and getting the QR-code
            while (true) {
                System.out.println("############################################");
                System.out.println("Choose operation:");
                System.out.println("1) Register facility");
                System.out.println("2) Generate QR-Code");
                System.out.println("3) Close");

                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1:
                        registerFacility();
                        break;
                    case 2:
                        generateQR();
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("This is not an option");
                }
                System.out.println("Operation complete");
                System.out.println();
                System.out.println();
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void getFacilityIdentifier() {
        String temp = "";
        System.out.println("Enter Facility identifier (10 numbers)");

        while (true) {
            try {
                temp = sc.nextLine();
                try {
                    Integer.parseInt(temp);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Facility identifier is not a number");
                }

                if (temp.length() != 10) throw new IllegalArgumentException("Facility identifier is not 10 digits long");

                facilityIdentifier = temp;
                return;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void connectToRegistrar() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", registrarPort);
        registrarConnection = (RegistrarConnection) registry
                .lookup("RegistrarService");
    }

    private void registerFacility() throws Exception {
        try {
            registrarConnection.registerCateringFacility(facilityIdentifier);
            System.out.println("Registered facility");
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void generateQR() throws Exception {
        try {
            //Get the day-specific pseudonyms
            getPseudonyms();

            //get the current day
            LocalDate day = LocalDate.now();

            //get today's pseudonym
            byte[] pseudonymToday = dailyPseudonyms.get(day);

            //Get the random number for today
            byte[] randomToday = getTodayRandom(day);

            //Get the identifier byte array
            byte[] facilityIdentifierBytes = facilityIdentifier.getBytes();

            //Generate and save the QR code
            qrCodeGenerator.setPath("Resources/QR/" + facilityIdentifier + ".png");
            qrCodeGenerator.generateQRCode(randomToday, facilityIdentifierBytes, pseudonymToday);

        } catch (Exception e) {
            handleException(e);
        }
    }

    private byte[] getTodayRandom(LocalDate cal) {
        //test if todays random is already created
        if (dailyRandoms.get(cal) == null) {
            //Generate new random key
            SecureRandom secureRandom = new SecureRandom();
            byte[] random = new byte[randomLength];
            secureRandom.nextBytes(random);

            //Place the key on the correct date
            dailyRandoms.put(cal, random);

            //Return the random
            return random;
        } else return dailyRandoms.get(cal);
    }


    private void getPseudonyms() throws Exception {
        //Get the current date index
        LocalDate day = LocalDate.now();

        //Get the year and month index from today
        int monthIndex = day.getMonthValue();
        int year = day.getYear();

        //Get the pseudonyms from the registrar service
        PseudonymUpdate pseudonymUpdate = registrarConnection.getPseudonyms(facilityIdentifier,year, monthIndex);

        //Set the pseudonyms hashmap
        dailyPseudonyms = pseudonymUpdate.getPseudonymHashmap();
    }


}
