package Controller.HelperObjects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Base64;

public class SymbolGenerator {
    private static byte[] keySignature;
    private static final int seedSize = 8;
    private static int[] generatorSeed;

    public void generateConfirmationSymbol(byte[] keySignature) {
        //initialize the parameters
        this.keySignature = keySignature;
        setGeneratorSeed();

        renderSymbols();

    }

    private void renderSymbols() {
        //For each iteration, use the value in the first index to generate a symbol
        //and use the value in the second index to generate the color of that symbol
        for (int i=0; i<generatorSeed.length; i+=2) {
            switch (generatorSeed[i]){
                case 0:
                    break;
                case 1:
                    break;
                case 2:
            }

        }
        //Of met SVG bestanden
    }

    private void setGeneratorSeed() {
        //Generate integer array from the byte array (per 4 bits)
        IntBuffer intBuf =
                ByteBuffer.wrap(keySignature)
                        .order(ByteOrder.BIG_ENDIAN)
                        .asIntBuffer();
        int[] temp = new int[intBuf.remaining()];
        intBuf.get(temp);

        //Only select the first few integers to generate the symbol
        generatorSeed = Arrays.copyOfRange(temp, 1, seedSize);
    }


}
