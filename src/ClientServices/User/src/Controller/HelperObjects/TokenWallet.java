package Controller.HelperObjects;

import Common.Messages.TokenUpdate;
import Common.Objects.Capsule;
import Common.Objects.Facility;
import Common.Objects.Token;

import java.io.IOException;
import java.security.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class TokenWallet {
    private static final int tokenDurationInSeconds = 60;
    private Facility currentFacility;
    private ArrayList<Token> tokens;
    private Stack<Token> unusedTokens;
    private PublicKey registrarPublicKey;
    private Capsule currentCapsule;



    public TokenWallet() {
        tokens = new ArrayList<>();
        unusedTokens = new Stack<>();
    }

    public void updateTokens(TokenUpdate tokenUpdate) {
        tokens = tokenUpdate.getTokens();

        //Generate a new random stack
        unusedTokens.addAll(tokenUpdate.getTokens());
        Collections.shuffle(unusedTokens);
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

    public void setCurrentFacility(String qrCodeString) {
        currentFacility = new Facility(qrCodeString);
    }


    public Capsule getCapsule() {
        if (currentFacility == null) throw new IllegalArgumentException("Can't activate tokens : no current facility");
        if (unusedTokens.empty()) throw new IllegalArgumentException("Can't activate tokens : no remaining tokens");

        Token selectedToken = unusedTokens.pop();
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime stopTime = startTime.plus(Duration.ofSeconds(tokenDurationInSeconds));
        byte[] facilityKey = currentFacility.getFacilityKey();

        return new Capsule(selectedToken, startTime, stopTime, facilityKey);
    }

    public void setCurrentCapsule(Capsule capsule) throws IOException {
        currentCapsule = capsule;
    }

    public Capsule getCurrentCapsule() {
        return currentCapsule;
    }

    public void leaveFacility() {
        //Reset current facility and active capsule
        this.currentCapsule = null;
        this.currentFacility = null;
    }

    public Facility getCurrentFacility() {
        return currentFacility;
    }
}
