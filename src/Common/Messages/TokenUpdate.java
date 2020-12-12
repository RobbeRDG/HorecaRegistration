package Common.Messages;

import Common.Objects.Token;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public class TokenUpdate implements Serializable {
    static final long serialVersionUID = 1L;
    private ArrayList<byte[]> tokenList;
    private ArrayList<byte[]> tokenSignatures;

    public TokenUpdate(ArrayList<byte[]> tokenList, ArrayList<byte[]> tokenSignatures) {
        this.tokenList = tokenList;
        this.tokenSignatures = tokenSignatures;
    }

    public ArrayList<byte[]> getTokens() {
        return tokenList;
    }

    public ArrayList<byte[]> getTokenSignatures() {
        return tokenSignatures;
    }
}
