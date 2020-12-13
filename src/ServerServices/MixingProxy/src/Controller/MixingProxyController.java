package Controller;

import Common.Messages.CapsuleVerification;
import Common.Objects.CapsuleLog;

import java.time.LocalDate;
import java.util.ArrayList;

public interface MixingProxyController {
    CapsuleVerification registerToken(CapsuleLog capsuleLog) throws Exception;

    void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception;
    void flushCapsules();

    void acknowledgeTokens(ArrayList<byte[]> acknowledgeTokens) throws Exception;
}
