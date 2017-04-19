package com.vevo.nibbler;


import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImageCropTest {
    @Test
    public void testCrop() {
        BufferedImage testImage = loadImage("src/test/resources/cropTest.jpg");
        int width = testImage.getWidth();

        BufferedImage cropped = ImageCrop.cropLetterBoxing(testImage, 0.04);
        int croppedHeight = cropped.getHeight();
        int croppedWidth = cropped.getWidth();

        assertEquals(549, croppedHeight);
        assertEquals(width, croppedWidth);
    }

    @Test
    public void testCrop2() {
        BufferedImage testImage = loadImage("src/test/resources/cropTest2.jpg");
        int width = testImage.getWidth();

        BufferedImage cropped = ImageCrop.cropLetterBoxing(testImage, 0.04);
        int croppedHeight = cropped.getHeight();
        int croppedWidth = cropped.getWidth();

        assertEquals(250, croppedHeight);
        assertEquals(width, croppedWidth);
    }

    @Test
    public void testNoCrop() {
        BufferedImage testImage = loadImage("src/test/resources/noCrop.jpg");
        BufferedImage cropped = ImageCrop.cropLetterBoxing(testImage, 0.04);
        assertTrue(testImage == cropped);
    }

    @Test
    public void testNoCropUneven() {
        BufferedImage testImage = loadImage("src/test/resources/cropTestUneven.jpg");
        BufferedImage cropped = ImageCrop.cropLetterBoxing(testImage, 0.04);
        assertTrue(testImage == cropped);
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
