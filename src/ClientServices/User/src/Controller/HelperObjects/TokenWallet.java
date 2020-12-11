package Controller.HelperObjects;

import Common.Messages.TokenUpdate;
import Common.Objects.CapsuleLog;
import Common.Objects.FacilityRegisterInformation;
import Common.Objects.Token;

import java.io.IOException;
import java.security.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class TokenWallet {
    private static final int tokenDurationInSeconds = 60;
    private FacilityRegisterInformation currentFacilityRegisterInformation;
    private ArrayList<Token> tokens;
    private Stack<Token> unusedTokens;
    private PublicKey registrarPublicKey;
    private CapsuleLog currentCapsuleLog;



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

    public void setCurrentFacility(FacilityRegisterInformation currentFacilityRegisterInformation) {
        this.currentFacilityRegisterInformation = currentFacilityRegisterInformation;
    }


    public CapsuleLog getCapsule() {
        if (currentFacilityRegisterInformation == null) throw new IllegalArgumentException("Can't activate tokens : no current facility");
        if (unusedTokens.empty()) throw new IllegalArgumentException("Can't activate tokens : no remaining tokens");

        Token selectedToken = unusedTokens.pop();
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime stopTime = startTime.plus(Duration.ofSeconds(tokenDurationInSeconds));
        byte[] facilityKey = currentFacilityRegisterInformation.getFacilityKey();

        return new CapsuleLog(selectedToken, startTime, stopTime, facilityKey);
    }

    public void setCurrentCapsule(CapsuleLog capsuleLog) throws IOException {
        currentCapsuleLog = capsuleLog;

        //Log the new active capsule
        logSpentCapsule();
    }

    private void logSpentCapsule() {

    }

    public CapsuleLog getCurrentCapsule() {
        return currentCapsuleLog;
    }

    public void leaveFacility() {
        //Reset current facility and active capsule
        this.currentCapsuleLog = null;
        this.currentFacilityRegisterInformation = null;
    }

    public FacilityRegisterInformation getCurrentFacility() {
        return currentFacilityRegisterInformation;
    }
}
