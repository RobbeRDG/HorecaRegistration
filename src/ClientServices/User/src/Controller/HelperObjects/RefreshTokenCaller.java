package Controller.HelperObjects;

import Controller.UserController;
import javafx.application.Platform;

import java.util.TimerTask;

public class RefreshTokenCaller extends TimerTask {
    public UserController userController;

    public RefreshTokenCaller(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                userController.refreshToken();
            }
        });
    }
}
