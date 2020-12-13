package Messages;

import Objects.InfectedUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class InfectedUserMessage implements Serializable {
    static final long serialVersionUID = 7L;
    private InfectedUser infectedUser;
    private byte[] signature;

    public InfectedUserMessage(InfectedUser infectedUser, byte[] signature) {
        this.infectedUser = infectedUser;
        this.signature = signature;
    }

    public InfectedUser getInfectedUser() {
        return infectedUser;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getInfectedUserBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(infectedUser);
        return out.toByteArray();
    }
}
