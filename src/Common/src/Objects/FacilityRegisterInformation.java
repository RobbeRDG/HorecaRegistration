package Objects;

import java.io.Serializable;
import java.util.Base64;

public class FacilityRegisterInformation implements Serializable {
    static final long serialVersionUID = 3L;
    private byte[] randomKey;
    private String facilityIdentifier;
    private byte[] facilityKey;

    public FacilityRegisterInformation(byte[] randomKey, String facilityIdentifier, byte[] facilityKey) {
        this.randomKey = randomKey;
        this.facilityIdentifier = facilityIdentifier;
        this.facilityKey = facilityKey;
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

    public String toBase64String() {
        String randomKeyString = Base64.getEncoder().encodeToString(randomKey);
        String facilityIdentifierString = Base64.getEncoder().encodeToString(facilityIdentifier.getBytes());
        String facilityKeyString = Base64.getEncoder().encodeToString(facilityKey);

        return randomKeyString + "," + facilityIdentifierString + "," + facilityKeyString;
    }

    public static FacilityRegisterInformation fromBase64String(String qrCodeString) {
        String[] QRStringArray = qrCodeString.split(",");

        byte[] randomKey = Base64.getDecoder().decode(QRStringArray[0]);
        String facilityIdentifier = new String(Base64.getDecoder().decode(QRStringArray[1]));
        byte[] facilityKey = Base64.getDecoder().decode(QRStringArray[2]);

        return new FacilityRegisterInformation(randomKey, facilityIdentifier, facilityKey);
    }
}
