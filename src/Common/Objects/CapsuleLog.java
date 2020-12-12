package Common.Objects;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Base64;

public class CapsuleLog implements Serializable {
    private byte[] token;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private byte[] facilityKey;

    public CapsuleLog(byte[] token, LocalDateTime startTime, LocalDateTime stopTime, byte[] facilityKey) {
        this.token = token;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.facilityKey = facilityKey;
    }

    public byte[] getToken() {
        return token;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public byte[] getFacilityKey() {
        return facilityKey;
    }

    public String toBase64String() {
        String tokenString = Base64.getEncoder().encodeToString(token);;
        String startTimeString = Base64.getEncoder().encodeToString(startTime.toString().getBytes());
        String stopTimeTimeString = Base64.getEncoder().encodeToString(stopTime.toString().getBytes());
        String facilityKeyString = Base64.getEncoder().encodeToString(facilityKey);

        return tokenString + "," + startTimeString + "," + stopTimeTimeString + "," + facilityKeyString;

    }

    public static CapsuleLog fromBase64String(String capsuleString) {
        String[] capsuleStringArray = capsuleString.split(",");

        byte[] token = Base64.getDecoder().decode(capsuleStringArray[0]);
        String startTimeString = new String(Base64.getDecoder().decode(capsuleStringArray[1]));
        LocalDateTime startTime = LocalDateTime.parse(startTimeString);
        String stopTimeString = new String(Base64.getDecoder().decode(capsuleStringArray[2]));
        LocalDateTime stopTime = LocalDateTime.parse(stopTimeString);
        byte[] facilityKey = Base64.getDecoder().decode(capsuleStringArray[3]);

        return new CapsuleLog(token, startTime, stopTime, facilityKey);
    }
}
