package Objects;

import Common.Messages.TokenUpdate;
import Common.Objects.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class TokenWallet {
    private ArrayList<Token> tokens;


    public TokenWallet() {
        tokens = new ArrayList<>();
    }

    public void updateTokens(TokenUpdate tokenUpdate) {
        tokens = tokenUpdate.getTokens();
    }

    public int getNumberOfTokens() {
        return tokens.size();
    }
}
