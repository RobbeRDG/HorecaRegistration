package Common.Messages;

import Common.Objects.Token;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public class TokenUpdate implements Serializable {
    static final long serialVersionUID = 1L;
    private ArrayList<Token> tokenList;

    public TokenUpdate(ArrayList<Token> tokens) {
        tokenList = tokens;
    }

    public ArrayList<Token> getTokens() {
        return tokenList;
    }
}
