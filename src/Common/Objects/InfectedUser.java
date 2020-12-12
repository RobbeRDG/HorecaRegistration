package Common.Objects;

import java.io.Serializable;
import java.util.ArrayList;

public class InfectedUser implements Serializable {
    private ArrayList<byte[]> infectedTokens;
    private ArrayList<FacilityVisitLog> infectedFacilityIntervals;

    public InfectedUser(ArrayList<byte[]> infectedTokens, ArrayList<FacilityVisitLog> infectedFacilityIntervals) {
        this.infectedTokens = infectedTokens;
        this.infectedFacilityIntervals = infectedFacilityIntervals;
    }

    public ArrayList<byte[]> getInfectedTokens() {
        return infectedTokens;
    }

    public ArrayList<FacilityVisitLog> getInfectedFacilityIntervals() {
        return infectedFacilityIntervals;
    }
}
