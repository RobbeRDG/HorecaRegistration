package Common.Objects;

import java.io.Serializable;

public class Token implements Serializable {
    static final long serialVersionUID = 1L;
    private byte[] tokenBytes;
    private byte[] signature;
    private boolean used;

    public Token(byte[] tokenBytes, byte[] signature, boolean used) {
        this.tokenBytes = tokenBytes;
        this.signature = signature;
        this.used = used;
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


}
