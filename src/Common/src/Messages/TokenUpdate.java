package Messages;

import java.io.Serializable;
import java.util.ArrayList;

public class TokenUpdate implements Serializable {
    static final long serialVersionUID = 5L;
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
