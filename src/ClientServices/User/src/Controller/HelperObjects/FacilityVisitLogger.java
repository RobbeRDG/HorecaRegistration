package Controller.HelperObjects;

import Common.Objects.Facility;
import Common.Objects.FacilityVisitLogEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class FacilityVisitLogger {
    private String logFileName;
    private Facility currentFacility;
    private LocalDateTime entryTime;
    private LocalDateTime leaveTime;
    private static final String logFileBasePath = "Resources/userLogs/facilityVisitLogs/";
    private static final int purgeLogsTimeInDays = 30;
    private static LocalDate purgeDate;

    public FacilityVisitLogger() {
        purgeDate = LocalDate.now().minusDays(purgeLogsTimeInDays);
    }

    public void startVisit(Facility currentFacility) {
        this.currentFacility = currentFacility;
        entryTime = LocalDateTime.now();
        leaveTime = null;
    }

    public void stopVisit() throws IOException {
        if (this.currentFacility == null | entryTime == null) throw new IllegalArgumentException("Can't execute leave facility logic: visit not initialized");
        else {
            leaveTime = LocalDateTime.now();
            //Refresh the log file
            refreshLogs();

            //Reset the visit logger for a new visit
            resetLogger();
        }
    }

    private void refreshLogs() throws IOException {
        File facilityVisitLogFile = new File(logFileBasePath + logFileName + ".txt");
        facilityVisitLogFile.createNewFile();

        ArrayList<FacilityVisitLogEntry> facilityVisitLogEntries = new ArrayList<>();
        facilityVisitLogEntries.add(new FacilityVisitLogEntry(entryTime, leaveTime, currentFacility));

        //Also read all previous logs
        Scanner sc = new Scanner(facilityVisitLogFile);
        while (sc.hasNextLine()) {
            facilityVisitLogEntries.add(new FacilityVisitLogEntry(sc.nextLine()));
        }
        sc.close();

        //Purge the expired logs
        facilityVisitLogEntries.removeIf(facilityVisitLogEntry -> facilityVisitLogEntry.getEntryTime().toLocalDate().isBefore(purgeDate));

        //write all logs to the file
        FileWriter fw = new FileWriter(facilityVisitLogFile);
        BufferedWriter out = new BufferedWriter(fw);
        for (FacilityVisitLogEntry facilityVisitLogEntry : facilityVisitLogEntries) {
            out.write(facilityVisitLogEntry.toBase64String());
            out.newLine();
        }
        out.close();
    }

    private void resetLogger() {
        currentFacility = null;
        entryTime = null;
        leaveTime = null;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }
}
