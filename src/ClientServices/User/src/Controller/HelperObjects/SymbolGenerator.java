package Controller.HelperObjects;

import javafx.beans.binding.Bindings;
import javafx.scene.CacheHint;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.abs;

public class SymbolGenerator {
    private static byte[] keySignature;
    private static int[] generatorSeed;

    public ImageView generateConfirmationSymbol(byte[] keySignature) throws FileNotFoundException {
        //initialize the parameters
        this.keySignature = keySignature;
        setGeneratorSeed();

        ImageView generatedSymbol = renderSymbol();
        return generatedSymbol;
    }

    private ImageView renderSymbol() throws FileNotFoundException {
        Image symbol = determineSymbolImage(generatorSeed[0]);
        Paint color = determineColorString(generatorSeed[1]);

        ImageView renderedSymbols = generateImageView(symbol, color);
        return renderedSymbols;
    }

    private ImageView generateImageView(Image symbol, Paint color) {
        //Setting the image view
        ImageView imageView = new ImageView(symbol);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);
        double xCoordinate = imageView.getX();
        double yCoordinate = imageView.getY();

        //Blackout the image
        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        //Blend the blackout and color change
        Blend colorAdjust = new Blend(
                BlendMode.MULTIPLY,
                monochrome,
                new ColorInput(
                        xCoordinate,
                        yCoordinate,
                        imageView.getFitWidth(),
                        imageView.getFitHeight(),
                        color
                )
        );

        //Apply the blend
        imageView.setEffect(colorAdjust);

        return imageView;
    }

    private void setGeneratorSeed() {
        //Generate integer from the byte array
        int keyInteger = java.nio.ByteBuffer.wrap(keySignature).getInt();
        int absoluteKeyInteger = abs(keyInteger);

        char[] keyCharArray = Integer.toString(absoluteKeyInteger).toCharArray();
        int symbolSeed = Character.getNumericValue(keyCharArray[keyCharArray.length - 2]);
        int colorSeed = Character.getNumericValue(keyCharArray[keyCharArray.length - 1]);

        //Only select the last 2 integers to generate the symbol
        generatorSeed = new int[]{symbolSeed, colorSeed};
    }

    private Color determineColorString(int colorValue) {
        switch (colorValue){
            case 0:
                return Color.DARKKHAKI;
            case 1:
                return Color.WHITE;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.BLUE;
            case 4:
                return Color.GREEN;
            case 5:
                return Color.MAGENTA;
            case 6:
                return Color.RED;
            case 7:
                return Color.ORANGE;
            case 8:
                return Color.CYAN;
            default:
                return Color.GRAY;
        }
    }

    private Image determineSymbolImage(int symbolValue) throws FileNotFoundException {
        String imageBasePath = "Resources/icons/";
        switch (symbolValue){
            case 0:
                return new Image(new FileInputStream(imageBasePath + "barbecue.png"));
            case 1:
                return new Image(new FileInputStream(imageBasePath + "board.png"));
            case 2:
                return new Image(new FileInputStream(imageBasePath + "burger.png"));
            case 3:
                return new Image(new FileInputStream(imageBasePath + "cheese.png"));
            case 4:
                return new Image(new FileInputStream(imageBasePath + "chefshat.png"));
            case 5:
                return new Image(new FileInputStream(imageBasePath + "coffee.png"));
            case 6:
                return new Image(new FileInputStream(imageBasePath + "cutlery.png"));
            case 7:
                return new Image(new FileInputStream(imageBasePath + "dinner.png"));
            case 8:
                return new Image(new FileInputStream(imageBasePath + "eggs.png"));
            default:
                return new Image(new FileInputStream(imageBasePath + "menu.png"));
        }
    }


}
