package Common.Objects;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Capsule implements Serializable {
    private Token token;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private byte[] facilityKey;

    public Capsule(Token token, LocalDateTime startTime, LocalDateTime stopTime, byte[] facilityKey) {
        this.token = token;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.facilityKey = facilityKey;
    }

    public Token getToken() {
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
}
