package HelperObjects;

import Objects.FacilityRegisterInformation;
import Objects.FacilityVisitLog;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class FacilityVisitLogger {
    private String logFileName;
    private FacilityRegisterInformation currentFacilityRegisterInformation;
    private LocalDateTime entryTime;
    private LocalDateTime leaveTime;
    private static final String logFileBasePath = "Resources/userLogs/facilityVisitLogs/";
    private static final int purgeLogsTimeInDays = 30;
    private static LocalDate purgeDate;

    public FacilityVisitLogger() {
        purgeDate = LocalDate.now().minusDays(purgeLogsTimeInDays);
    }

    public void startVisit(FacilityRegisterInformation currentFacilityRegisterInformation) {
        this.currentFacilityRegisterInformation = currentFacilityRegisterInformation;
        entryTime = LocalDateTime.now();
        leaveTime = null;
    }

    public void stopVisit() throws IOException {
        if (this.currentFacilityRegisterInformation == null | entryTime == null) throw new IllegalArgumentException("Can't execute leave facility logic: visit not initialized");
        else {
            leaveTime = LocalDateTime.now();

            //Refresh the log file
            writeLogs();

            //Reset the visit logger for a new visit
            resetLogger();
        }
    }

    private void writeLogs() throws IOException {
        //Create the log file
        File facilityVisitLogFile = new File(logFileBasePath + logFileName + ".txt");
        facilityVisitLogFile.createNewFile();

        //Read all previous logs
        ArrayList<FacilityVisitLog> facilityVisitLogEntries = readFacilityVisitLogsFromFile(facilityVisitLogFile);

        //Purge the expired logs
        facilityVisitLogEntries.removeIf(facilityVisitLog -> facilityVisitLog.getEntryTime().toLocalDate().isBefore(purgeDate));

        //Add the new facility entry
        facilityVisitLogEntries.add(new FacilityVisitLog(entryTime, leaveTime, currentFacilityRegisterInformation));

        //write all logs to the file
        FileWriter fw = new FileWriter(facilityVisitLogFile);
        BufferedWriter out = new BufferedWriter(fw);
        for (FacilityVisitLog facilityVisitLog : facilityVisitLogEntries) {
            out.write(facilityVisitLog.toBase64String());
            out.newLine();
        }
        out.close();
    }

    private void resetLogger() {
        currentFacilityRegisterInformation = null;
        entryTime = null;
        leaveTime = null;
    }

    public ArrayList<FacilityVisitLog> readFacilityVisitLogsFromFile(File facilityVisitLogFile) throws FileNotFoundException {
        ArrayList<FacilityVisitLog> facilityVisitLogs = new ArrayList<>();

        Scanner sc = new Scanner(facilityVisitLogFile);
        while (sc.hasNextLine()) {
            facilityVisitLogs.add(FacilityVisitLog.fromBase64String(sc.nextLine()));
        }
        sc.close();

        return facilityVisitLogs;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }
}
