package Common.Objects;

import java.util.Base64;

public class Facility {
    private byte[] randomKey;
    private String facilityIdentifier;
    private byte[] facilityKey;

    public Facility(String qrCodeString) {
        String[] QRStringArray = qrCodeString.split(",");

        randomKey = Base64.getDecoder().decode(QRStringArray[0]);
        facilityIdentifier = new String(Base64.getDecoder().decode(QRStringArray[1]));
        facilityKey = Base64.getDecoder().decode(QRStringArray[2]);
    }

    public byte[] getRandomKey() {
        return randomKey;
    }

    public String getFacilityIdentifier() {
        return facilityIdentifier;
    }

    public byte[] getFacilityKey() {
        return facilityKey;
    }
}
