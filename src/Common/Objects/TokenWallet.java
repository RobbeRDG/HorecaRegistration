package Common.Objects;

import Common.Messages.TokenUpdate;
import Common.Objects.Token;

import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TokenWallet {
    private ArrayList<Token> tokens;
    private PublicKey registrarPublicKey;


    public TokenWallet() {
        tokens = new ArrayList<>();
    }

    public void updateTokens(TokenUpdate tokenUpdate) {
        tokens = tokenUpdate.getTokens();
    }

    public int getNumberOfTokens() {
        return tokens.size();
    }

    public boolean signaturesMatch() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        for( Token token : tokens) {
            byte[] signature = token.getSignature();
            sign.initVerify(registrarPublicKey);
            sign.update(token.getTokenBytes());

            if (!sign.verify(signature)) return false;
        }

        return true;
    }

    public void setRegistrarPublicKey(PublicKey registrarPublicKey) {
        this.registrarPublicKey = registrarPublicKey;
    }
}
