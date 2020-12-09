package Controller;

import Common.Exceptions.CapsuleNotValidException;
import Common.Messages.CapsuleVerification;
import Common.Objects.Capsule;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface MixingProxyController {
    CapsuleVerification registerToken(Capsule capsule) throws Exception;

    void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception;
}
