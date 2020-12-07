package Common.Messages;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public class TokenUpdate implements Serializable {
    private ArrayList<byte[]> tokens;
    private HashMap<byte[], byte[]> tokenSignatures;

    public TokenUpdate(ArrayList<byte[]> tokens, HashMap<byte[], byte[]> tokenSignatures) {
        this.tokens = tokens;
        this.tokenSignatures = tokenSignatures;
    }
}
