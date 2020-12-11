package Controller.HelperObjects;

import Common.Objects.CapsuleLog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        File spentCapsuleLogFile = new File(logFileBasePath + logFileName + ".txt");
        spentCapsuleLogFile.createNewFile();

        ArrayList<CapsuleLog> spentCapsuleLogEntries = new ArrayList<>();

        //Read all previous logs
        Scanner sc = new Scanner(spentCapsuleLogFile);
        while (sc.hasNextLine()) {
            spentCapsuleLogEntries.add(CapsuleLog.fromBase64String(sc.nextLine()));
        }
        sc.close();

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
}
