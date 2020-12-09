package Common.Messages;

import java.io.Serializable;

public class CapsuleVerification implements Serializable {
    private byte[] facilityKey;
    private byte[] keySignature;

    public CapsuleVerification(byte[] facilityKey, byte[] keySignature) {
        this.facilityKey = facilityKey;
        this.keySignature = keySignature;
    }

    public byte[] getFacilityKey() {
        return facilityKey;
    }

    public byte[] getKeySignature() {
        return keySignature;
    }
}
