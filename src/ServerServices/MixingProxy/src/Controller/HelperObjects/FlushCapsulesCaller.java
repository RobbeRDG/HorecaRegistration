package Controller.HelperObjects;

import Controller.MixingProxyController;

import java.util.TimerTask;

public class FlushCapsulesCaller extends TimerTask {
    private MixingProxyController mixingProxyController;

    public FlushCapsulesCaller(MixingProxyController mixingProxyController) {
        this.mixingProxyController = mixingProxyController;
    }

    @Override
    public void run() {
        mixingProxyController.flushCapsules();
    }
}

