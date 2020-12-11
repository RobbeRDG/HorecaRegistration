package Controller.HelperObjects;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;

public class QRGenerator {
    private static String path;
    public QRGenerator(){
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void generateQRCode(byte[] randomToday, byte[] facilityIdentifierBytes, byte[] facilityKey) throws WriterException, IOException {
        //Get the Base64 encoding of each binary array
        String randomTodayStr = Base64.getEncoder().encodeToString(randomToday);
        String facilityIdentifierBytesSTR = Base64.getEncoder().encodeToString(facilityIdentifierBytes);
        String facilityKeyStr = Base64.getEncoder().encodeToString(facilityKey);

        String QRMessage = randomTodayStr + "," + facilityIdentifierBytesSTR + "," + facilityKeyStr;

        //Generate the QR matrix
        QRCodeWriter QRWriter = new QRCodeWriter();
        BitMatrix bitMatrix = QRWriter.encode(QRMessage, BarcodeFormat.QR_CODE, 200, 200);

        //Save the QR code
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", Paths.get(path));
    }
}
