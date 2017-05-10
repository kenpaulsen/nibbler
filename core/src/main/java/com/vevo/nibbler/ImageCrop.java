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

    public static  BufferedImage cropPillarBoxing(BufferedImage source) {
        return cropPillarBoxing(source, DEFAULT_TOLERANCE);
    }

    static BufferedImage cropLetterBoxing(BufferedImage source, double tolerance) {
        int width = source.getWidth();
        int height = source.getHeight();

        // try to find the top letter boxing
        int topBorder = scanTop(source, tolerance);

        // if this isn't letter boxed, do nothing
        if (topBorder < 5) {
            return source;
        }

        int bottomBorder = scanBottom(source, tolerance);

        // if the borders aren't very close, something probably went wrong
        if (Math.abs(topBorder - bottomBorder) > 10) {
            return source;
        }

        BufferedImage destination = new BufferedImage(width, height - topBorder - bottomBorder, source.getType());

        destination.getGraphics().drawImage(source, 0, 0,
                destination.getWidth(), destination.getHeight(),
                0, bottomBorder, width, height - topBorder, null);

        return destination;
    }

    static BufferedImage cropPillarBoxing(BufferedImage source, double tolerance) {
        int width = source.getWidth();
        int height = source.getHeight();

        // try to find the top letter boxing
        int leftBorder = scanLeft(source, tolerance);

        // if this isn't letter boxed, do nothing
        if (leftBorder < 5) {
            return source;
        }

        int rightBorder = scanRight(source, tolerance);

        // if the borders aren't very close, something probably went wrong
        if (Math.abs(rightBorder - leftBorder) > 10) {
            return source;
        }

        BufferedImage destination = new BufferedImage(width - leftBorder - rightBorder, height, source.getType());

        destination.getGraphics().drawImage(source, 0, 0,
                destination.getWidth(), destination.getHeight(),
                leftBorder, 0, width - rightBorder, height, null);

        return destination;
    }


    private static boolean scanColumn(BufferedImage source, int x, double tolerance) {
        int height = source.getHeight();
        for (int y = 0; y < height; y += 5) {
            if (!isBlack(source.getRGB(x, y), tolerance)) return false;
        }
        return true;
    }


    private static boolean scanRow(BufferedImage source, int y, double tolerance) {
        int width = source.getWidth();
        for (int x = 0; x < width; x += 5) {
            if (!isBlack(source.getRGB(x, y), tolerance)) return false;
        }
        return true;
    }


    private static int scanLeft(BufferedImage source, double tolerance) {
        int width = source.getWidth();
        int leftColumn = 0;
        for (int x = 0; x < width / 3; x++) {
            if(!scanColumn(source, x, tolerance)) break;
            leftColumn = x;
        }
        return leftColumn;
    }

    private static int scanRight(BufferedImage source, double tolerance) {
        int width = source.getWidth();
        int rightColumn =0;
        // try to find the top letter boxing
        for (int x = width - 1; x > (2 * width) / 3; x--) {
            if(!scanColumn(source, x, tolerance)) break;
            rightColumn = width -x;
        }
        return rightColumn;
    }

    private static int scanBottom(BufferedImage source, double tolerance) {
        int height = source.getHeight();
        int bottomBorder =0;
        // try to find the top letter boxing
        for (int y = 0; y < height / 3; y++) {
            if(!scanRow(source, y, tolerance)) break;
            bottomBorder = y;
        }
        return bottomBorder;
    }

    private static int scanTop(BufferedImage source, double tolerance) {
        int height = source.getHeight();
        int topBorder = 0;
        // and the bottom
        for (int y = height - 1; y > (2 * height) / 3; y--) {
            if(!scanRow(source, y, tolerance)) break;
            topBorder = height - y;
        }
        return  topBorder;
    }


    private static boolean isBlack(int b, double tolerance) {
        int aAlpha  = (BLACK & 0xFF000000) >>> 24;   // Alpha level
        int aRed    = (BLACK & 0x00FF0000) >>> 16;   // Red level
        int aGreen  = (BLACK & 0x0000FF00) >>> 8;    // Green level
        int aBlue   = BLACK & 0x000000FF;            // Blue level

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
        double percentAway = distance / 510.0d;

        return percentAway < tolerance;
    }
}