package HelperObjects;

import Objects.CapsuleLog;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class SpentCapsuleLogger {
    private String logFileName;
    private static final String logFileBasePath = "Resources/userLogs/spentTokenLogs/";
    private static final int purgeLogsTimeInDays = 30;
    private static LocalDate purgeDate;

    public SpentCapsuleLogger() {
        purgeDate = LocalDate.now().minusDays(purgeLogsTimeInDays);
    }

    public void logCapsule(CapsuleLog capsuleLog) throws IOException {
        //Create the log file
        File spentCapsuleLogFile = new File(logFileBasePath + logFileName + ".txt");
        spentCapsuleLogFile.createNewFile();

        //Read all previous logs
        ArrayList<CapsuleLog> spentCapsuleLogEntries = readCapsuleLogsFromFile(spentCapsuleLogFile);

        //Purge the expired logs
        spentCapsuleLogEntries.removeIf(spentCapsuleLog -> spentCapsuleLog.getStartTime().toLocalDate().isBefore(purgeDate));

        //Add the new Capsule
        spentCapsuleLogEntries.add(capsuleLog);

        //write all logs to the file
        FileWriter fw = new FileWriter(spentCapsuleLogFile);
        BufferedWriter out = new BufferedWriter(fw);
        for (CapsuleLog spentCapsuleLog : spentCapsuleLogEntries) {
            out.write(spentCapsuleLog.toBase64String());
            out.newLine();
        }
        out.close();
    }

    public ArrayList<CapsuleLog> readCapsules() throws FileNotFoundException {
        File spentCapsuleLogFile = new File(logFileBasePath + logFileName + ".txt");
        return readCapsuleLogsFromFile(spentCapsuleLogFile);
    }

    public static ArrayList<CapsuleLog> readCapsuleLogsFromFile(File capsuleLogFile) throws FileNotFoundException {
        ArrayList<CapsuleLog> capsuleLogs = new ArrayList<>();

        Scanner sc = new Scanner(capsuleLogFile);
        while (sc.hasNextLine()) {
            capsuleLogs.add(CapsuleLog.fromBase64String(sc.nextLine()));
        }
        sc.close();

        return capsuleLogs;
    }

    public static ArrayList<byte[]> readOnlySpentTokensFromFile(File capsuleLogFile) throws FileNotFoundException {
        ArrayList<byte[]> capsuleLogs = new ArrayList<>();

        Scanner sc = new Scanner(capsuleLogFile);
        while (sc.hasNextLine()) {
            capsuleLogs.add(CapsuleLog.fromBase64String(sc.nextLine()).getToken());
        }
        sc.close();

        return capsuleLogs;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }
}
