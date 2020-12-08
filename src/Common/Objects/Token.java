package Common.Objects;

import java.io.Serializable;

public class Token implements Serializable {
    static final long serialVersionUID = 1L;
    private byte[] bytes;
    private byte[] signature;
    private boolean used;

    public Token(byte[] bytes, byte[] signature, boolean used) {
        this.bytes = bytes;
        this.signature = signature;
        this.used = used;
    }


}
