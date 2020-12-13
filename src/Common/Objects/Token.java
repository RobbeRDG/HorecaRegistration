package Common.Objects;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

public class Token implements Serializable {
    static final long serialVersionUID = 9L;
    private byte[] tokenBytes;
    private byte[] signature;
    private LocalDate date;

    public Token(byte[] tokenBytes, byte[] signature, LocalDate date) {
        this.tokenBytes = tokenBytes;
        this.signature = signature;
        this.date = date;
    }

    public static Token fromBase64String(String tokenString) {
        String[] tokenStringArray = tokenString.split("/");

        byte[] tokenBytes = Base64.getDecoder().decode(tokenStringArray[0]);
        byte[] signature = Base64.getDecoder().decode(tokenStringArray[1]);
        String dateString = new String(Base64.getDecoder().decode(tokenStringArray[2]));
        LocalDate date = LocalDate.parse(dateString);

        return new Token(tokenBytes, signature, date);
    }

    public byte[] getTokenBytes() {
        return tokenBytes;
    }

    public byte[] getSignature() {
        return signature;
    }

    public LocalDate getDate() {
        return date;
    }

    public String toBase64String() {
        String tokenBytesString = Base64.getEncoder().encodeToString(tokenBytes);
        String signatureString = Base64.getEncoder().encodeToString(signature);
        String dateString = Base64.getEncoder().encodeToString(date.toString().getBytes());

        return tokenBytesString + "/" + signatureString + "/" + dateString;
    }
}
