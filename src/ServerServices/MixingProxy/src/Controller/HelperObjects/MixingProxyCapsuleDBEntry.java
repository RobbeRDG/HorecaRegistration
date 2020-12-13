package Controller.HelperObjects;

import java.time.LocalDateTime;
import javax.xml.bind.DatatypeConverter;

public class MixingProxyCapsuleDBEntry {
    private String token;
    private String facilityKey;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;



    public MixingProxyCapsuleDBEntry(byte[] token, byte[] facilityKey, LocalDateTime startTime, LocalDateTime stopTime) {
        this.token = DatatypeConverter.printHexBinary(token);
        this.facilityKey = DatatypeConverter.printHexBinary(facilityKey);
        this.startTime = startTime;
        this.stopTime = stopTime;
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
}
