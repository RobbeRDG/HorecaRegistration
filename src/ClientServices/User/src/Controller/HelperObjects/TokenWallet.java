package Controller.HelperObjects;

import Common.Messages.TokenUpdate;
import Common.Objects.Capsule;
import Common.Objects.Facility;
import Common.Objects.Token;

import java.security.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TokenWallet {
    private static final int tokenDurationInSeconds = 3600;
    private Facility currentFacility;
    private ArrayList<Token> tokens;
    private Stack<Token> unusedTokens;
    private PublicKey registrarPublicKey;
    private Token activeToken;
    private ArrayList<Capsule> usedTokens;


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

    public void setCurrentFacility(String qrCodeString) {
        currentFacility = new Facility(qrCodeString);
    }

    public Capsule activateTokens() {
        if (currentFacility == null) throw new IllegalArgumentException("Can't activate tokens : no current facility");
        if (unusedTokens.empty()) throw new IllegalArgumentException("Can't activate tokens : no remaining tokens");

        activeToken = unusedTokens.pop();
        return buildCapsule();
    }

    private Capsule buildCapsule() {
        Token token = activeToken;
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime stopTime = startTime.plus(Duration.ofSeconds(tokenDurationInSeconds));
        byte[] facilityKey = currentFacility.getFacilityKey();

        return new Capsule(token, startTime, stopTime, facilityKey);
    }

    public int getNumberOfRemainingTokens() {
        return unusedTokens.size();
    }
}
