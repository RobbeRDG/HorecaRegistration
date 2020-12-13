package Objects;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Base64;

public class FacilityVisitLog implements Serializable {
    static final long serialVersionUID = 2L;
    private LocalDateTime entryTime;
    private LocalDateTime leaveTime;
    private FacilityRegisterInformation visitedFacilityRegisterInformation;


    public FacilityVisitLog(LocalDateTime entryTime, LocalDateTime leaveTime, FacilityRegisterInformation visitedFacilityRegisterInformation) {
        this.entryTime = entryTime;
        this.leaveTime = leaveTime;
        this.visitedFacilityRegisterInformation = visitedFacilityRegisterInformation;
    }


    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getLeaveTime() {
        return leaveTime;
    }

    public FacilityRegisterInformation getVisitedFacility() {
        return visitedFacilityRegisterInformation;
    }

    public String toBase64String() {
        String entryTimeString = Base64.getEncoder().encodeToString(entryTime.toString().getBytes());
        String leaveTimeString = Base64.getEncoder().encodeToString(leaveTime.toString().getBytes());
        return entryTimeString + ";" + leaveTimeString + ";" + visitedFacilityRegisterInformation.toBase64String();
    }

    public static FacilityVisitLog fromBase64String(String entryString) {
        String[] entryStringArray = entryString.split(";");

        //Parse strings of entry and leave times
        String entryDateString = new String(Base64.getDecoder().decode(entryStringArray[0]));
        LocalDateTime entryTime = LocalDateTime.parse(entryDateString);
        String leaveDateString = new String(Base64.getDecoder().decode(entryStringArray[1]));
        LocalDateTime leaveTime = LocalDateTime.parse(leaveDateString);

        //Get the facility from the string
        FacilityRegisterInformation visitedFacilityRegisterInformation = FacilityRegisterInformation.fromBase64String(entryStringArray[2]);

        return new FacilityVisitLog(entryTime, leaveTime, visitedFacilityRegisterInformation);
    }
}
