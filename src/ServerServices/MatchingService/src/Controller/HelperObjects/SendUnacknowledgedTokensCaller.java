package Controller.HelperObjects;

import Controller.MatchingServiceController;

import java.util.TimerTask;

public class SendUnacknowledgedTokensCaller extends TimerTask {
    private MatchingServiceController matchingServiceController;

    public SendUnacknowledgedTokensCaller(MatchingServiceController matchingServiceController) {
        this.matchingServiceController = matchingServiceController;
    }

    @Override
    public void run() {
        matchingServiceController.sendUnacknowledgedTokens();
    }
}
