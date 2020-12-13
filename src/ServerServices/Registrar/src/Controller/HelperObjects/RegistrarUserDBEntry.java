package Controller.HelperObjects;

public class RegistrarUserDBEntry {
    private String userIdentifier;

    public RegistrarUserDBEntry(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }
}
