package Controller.HelperObjects;

import Controller.MatchingServiceController;

import java.util.TimerTask;

public class DeleteExpiredCaller extends TimerTask {
    private MatchingServiceController matchingServiceController;

    public DeleteExpiredCaller(MatchingServiceController matchingServiceController) {
        this.matchingServiceController = matchingServiceController;
    }

    @Override
    public void run() {
        matchingServiceController.deleteExpiredCapsules();
    }
}
