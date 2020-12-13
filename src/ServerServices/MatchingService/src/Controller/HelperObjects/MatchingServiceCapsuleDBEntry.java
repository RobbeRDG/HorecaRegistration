package Controller.HelperObjects;

import javax.xml.bind.DatatypeConverter;
import java.time.LocalDateTime;

public class MatchingServiceCapsuleDBEntry {
    private String token;
    private String facilityKey;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private boolean critical;
    private boolean informed;


    public MatchingServiceCapsuleDBEntry(byte[] token, byte[] facilityKey, LocalDateTime startTime, LocalDateTime stopTime, boolean critical, boolean informed) {
        this.token = DatatypeConverter.printHexBinary(token);
        this.facilityKey = DatatypeConverter.printHexBinary(facilityKey);
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.critical = critical;
        this.informed = informed;
    }

    public String getToken() {
        return token;
    }

    public String getFacilityKey() {
        return facilityKey;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public boolean isCritical() {
        return critical;
    }

    public boolean isInformed() {
        return informed;
    }
}
