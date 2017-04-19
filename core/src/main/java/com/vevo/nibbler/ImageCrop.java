package com.vevo.nibbler;

import java.awt.image.BufferedImage;
import java.awt.Color;
public class ImageCrop {

    private ImageCrop() {}

    private static final double DEFAULT_TOLERANCE = 0.04;
    private static final int BLACK = Color.BLACK.getRGB();

    public static  BufferedImage cropLetterBoxing(BufferedImage source) {
        return cropLetterBoxing(source, DEFAULT_TOLERANCE);
    }

    public static BufferedImage cropLetterBoxing(BufferedImage source, double tolerance) {
        int width = source.getWidth();
        int height = source.getHeight();
        int topBorder = 0;
        int bottomBorder = 0;
        Integer black = null;

        // try to find a good black level
        for (int i = 0; i < width; i +=5) {
            int pixel = source.getRGB(i, 5);

            if (distance(BLACK, pixel) < 0.1) {
                black = pixel;
            } else {
                black = null;
                break;
            }
        }

        if (black == null) {
            return source;
        }

        // try to find the top letter boxing
        for (int y = 0; y < height / 3; y++) {
            boolean fullRow = true;

            for (int x = 0; x < width; x += 5) {
                if (!isBlack(black, source.getRGB(x, y), tolerance)) {
                    fullRow = false;
                    break;
                }
            }

            if (fullRow) {
                topBorder = y;
            } else {
                break;
            }
        }

        // if this isn't letter boxed, do nothing
        if (topBorder < 5) {
            return source;
        }

        // and the bottom
        for (int y = height - 1; y > (2 * height) / 3; y--) {
            boolean fullRow = true;

            for (int x = 0; x < width; x += 5) {
                if (!isBlack(black, source.getRGB(x, y), tolerance)) {
                    fullRow = false;
                    break;
                }
            }

            if (fullRow) {
                bottomBorder = height - y;
            } else {
                break;
            }
        }

        // if the borders aren't very close, something probably went wrong
        if (Math.abs(topBorder - bottomBorder) > 10) {
            return source;
        }

        BufferedImage destination = new BufferedImage(width, height - topBorder - bottomBorder, source.getType());

        destination.getGraphics().drawImage(source, 0, 0,
                destination.getWidth(), destination.getHeight(),
                0, topBorder, width, height - bottomBorder, null);

        return destination;
    }

    private static double distance(int a, int b) {
        int aAlpha  = (a & 0xFF000000) >>> 24;   // Alpha level
        int aRed    = (a & 0x00FF0000) >>> 16;   // Red level
        int aGreen  = (a & 0x0000FF00) >>> 8;    // Green level
        int aBlue   = a & 0x000000FF;            // Blue level

        int bAlpha  = (b & 0xFF000000) >>> 24;   // Alpha level
        int bRed    = (b & 0x00FF0000) >>> 16;   // Red level
        int bGreen  = (b & 0x0000FF00) >>> 8;    // Green level
        int bBlue   = b & 0x000000FF;            // Blue level

        double distance = Math.sqrt((aAlpha-bAlpha)*(aAlpha-bAlpha) +
                (aRed-bRed)*(aRed-bRed) +
                (aGreen-bGreen)*(aGreen-bGreen) +
                (aBlue-bBlue)*(aBlue-bBlue));

        // 510.0 is the maximum distance between two colors
        // (0,0,0,0 -> 255,255,255,255)
        return distance / 510.0d;
    }

    private static boolean isBlack(int black, int b, double tolerance) {
        return distance(black, b) < tolerance;
    }
}
