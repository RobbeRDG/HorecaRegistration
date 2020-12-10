package Common.Objects;

import sun.rmi.runtime.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class FacilityVisitLogEntry {
    private LocalDateTime entryTime;
    private LocalDateTime leaveTime;
    private Facility visitedFacility;

    public FacilityVisitLogEntry(String entryString) {
        String[] entryStringArray = entryString.split(";");

        //Parse strings of entry and leave times
        String entryDateString = new String(Base64.getDecoder().decode(entryStringArray[0]));
        entryTime = LocalDateTime.parse(entryDateString);
        String leaveDateString = new String(Base64.getDecoder().decode(entryStringArray[1]));
        leaveTime = LocalDateTime.parse(leaveDateString);

        //Get the facility from the string
        visitedFacility = new Facility(entryStringArray[2]);
    }

    public FacilityVisitLogEntry(LocalDateTime entryTime, LocalDateTime leaveTime, Facility visitedFacility) {
        this.entryTime = entryTime;
        this.leaveTime = leaveTime;
        this.visitedFacility = visitedFacility;
    }


    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public Facility getVisitedFacility() {
        return visitedFacility;
    }

    public String toBase64String() {
        String entryTimeString = Base64.getEncoder().encodeToString(entryTime.toString().getBytes());
        String leaveTimeString = Base64.getEncoder().encodeToString(leaveTime.toString().getBytes());
        return entryTimeString + ";" + leaveTimeString + ";" + visitedFacility.toBase64String();
    }
}
