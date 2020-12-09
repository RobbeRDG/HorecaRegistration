package Common.Objects;

import java.io.Serializable;
import java.time.LocalDate;

public class Token implements Serializable {
    static final long serialVersionUID = 1L;
    private byte[] tokenBytes;
    private byte[] signature;
    private boolean used;
    private LocalDate date;

    public Token(byte[] tokenBytes, byte[] signature, LocalDate date, boolean used) {
        this.tokenBytes = tokenBytes;
        this.signature = signature;
        this.used = used;
        this.date = date;
    }

    public byte[] getTokenBytes() {
        return tokenBytes;
    }

    public byte[] getSignature() {
        return signature;
    }

    public boolean isUsed() {
        return used;
    }

    public LocalDate getDate() {
        return date;
    }
}
