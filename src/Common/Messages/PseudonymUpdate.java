package Common.Messages;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;

public class PseudonymUpdate implements Serializable {
    private HashMap<LocalDate, byte[]> pseudonyms;
    public PseudonymUpdate(HashMap<LocalDate, byte[]> pseudonyms) {
        this.pseudonyms = pseudonyms;
    }
    public HashMap<LocalDate, byte[]> getPseudonymHashmap() {
        return pseudonyms;
    }
}
