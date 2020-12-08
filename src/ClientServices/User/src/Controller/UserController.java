package Controller;

import java.io.IOException;

public interface UserController {

    void showLogin() throws IOException;
    void showApp() throws IOException;
    void registerUSer(String userIdentifier) throws Exception;

    void setUserIdentifier(String phoneNumber);

    void getTodaysTokens() throws Exception;
    void scanQR();
}
