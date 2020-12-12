package Controller;

import com.google.zxing.NotFoundException;

import java.io.IOException;
import java.security.PublicKey;

public interface UserController {

    void showLogin() throws IOException;
    void showApp() throws IOException;
    void registerUSer(String userIdentifier) throws Exception;

    void setUserIdentifier(String phoneNumber);

    void getTodaysTokens() throws Exception;
    void registerToFacility() throws Exception;

    void leaveFacility() throws Exception;
    void refreshToken();
    PublicKey getRegistrarPublicKey();
}
