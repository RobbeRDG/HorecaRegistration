package Controller.HelperObjects;

import Messages.TokenUpdate;
import Objects.CapsuleLog;
import Objects.FacilityRegisterInformation;
import Controller.UserController;

import java.io.IOException;
import java.security.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class TokenWallet {
    private UserController userController;
    private static final int tokenDurationInSeconds = 60;
    private FacilityRegisterInformation currentFacilityRegisterInformation;
    private ArrayList<byte[]> tokens;
    private Stack<byte[]> unusedTokens;
    private CapsuleLog currentCapsuleLog;



    public TokenWallet(UserController userController) {
        this.userController = userController;
        tokens = new ArrayList<>();
        unusedTokens = new Stack<>();
    }

    public void updateTokens(TokenUpdate tokenUpdate) {
        tokens = tokenUpdate.getTokens();

        //Generate a new random stack
        unusedTokens.addAll(tokenUpdate.getTokens());
        Collections.shuffle(unusedTokens);
    }

    public boolean signaturesMatch(TokenUpdate tokenUpdate) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        ArrayList<byte[]> verifyTokens = tokenUpdate.getTokens();
        ArrayList<byte[]> signatures = tokenUpdate.getTokenSignatures();

        //If the arrays are a different size the tokens can't be verified
        if (verifyTokens.size() != signatures.size()) return false;

        Signature sign = Signature.getInstance("SHA256withRSA");
        for (int i=0; i<verifyTokens.size(); i++) {
            byte[] token = verifyTokens.get(i);
            byte[] signature = signatures.get(i);

            sign.initVerify(userController.getRegistrarPublicKey());
            sign.update(token);

            if (!sign.verify(signature)) return false;
        }

        return true;
    }

    public void setCurrentFacility(FacilityRegisterInformation currentFacilityRegisterInformation) {
        this.currentFacilityRegisterInformation = currentFacilityRegisterInformation;
    }


    public CapsuleLog getCapsule() {
        if (currentFacilityRegisterInformation == null) throw new IllegalArgumentException("Can't activate tokens : no current facility");
        if (unusedTokens.empty()) throw new IllegalArgumentException("Can't activate tokens : no remaining tokens");

        byte[] selectedToken = unusedTokens.pop();
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
