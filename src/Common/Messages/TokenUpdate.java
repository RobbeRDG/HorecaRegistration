package Common.Messages;

import Common.Objects.Token;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public class TokenUpdate implements Serializable {
    static final long serialVersionUID = 1L;
    private ArrayList<Token> tokenList;

    public TokenUpdate(ArrayList<byte[]> tokens, HashMap<byte[], byte[]> tokenSignatures) {
        ArrayList<Token> tokenArrayList = new ArrayList<>();
        for (byte[] token : tokens) {
            byte[] signature = tokenSignatures.get(token);

            tokenArrayList.add(new Token(token, signature, false));
        }

        tokenList = tokenArrayList;
    }

    public ArrayList<Token> getTokens() {
        return tokenList;
    }
}
