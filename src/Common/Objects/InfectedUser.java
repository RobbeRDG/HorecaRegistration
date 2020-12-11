package Common.Objects;

import java.io.Serializable;
import java.util.ArrayList;

public class InfectedUser implements Serializable {
    private ArrayList<CapsuleLog> infectedCapsules;
    private ArrayList<FacilityVisitLog> infectedFacilities;

    public InfectedUser(ArrayList<CapsuleLog> infectedCapsules, ArrayList<FacilityVisitLog> infectedFacilities) {
        this.infectedCapsules = infectedCapsules;
        this.infectedFacilities = infectedFacilities;
    }

    public ArrayList<CapsuleLog> getInfectedCapsules() {
        return infectedCapsules;
    }

    public ArrayList<FacilityVisitLog> getInfectedFacilities() {
        return infectedFacilities;
    }
}
