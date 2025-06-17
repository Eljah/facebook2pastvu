package com.example.facebook2pastvu;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Simple average hash based matcher to avoid external dependencies.
 */
public class ImageMatcher {
    private static final int SIZE = 8;

    public String hash(Path image) throws IOException {
        BufferedImage img = ImageIO.read(image.toFile());
        return hash(img);
    }

    public boolean isDuplicate(Path local, String remoteUrl) throws IOException {
        BufferedImage img1 = ImageIO.read(local.toFile());
        BufferedImage img2 = ImageIO.read(new URL(remoteUrl));
        String h1 = hash(img1);
        String h2 = hash(img2);
        return hamming(h1, h2) <= 5;
    }

    private String hash(BufferedImage img) {
        BufferedImage small = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = small.createGraphics();
        g.drawImage(img, 0, 0, SIZE, SIZE, null);
        g.dispose();

        int[] pixels = small.getRaster().getPixels(0, 0, SIZE, SIZE, (int[]) null);
        int avg = 0;
        for (int p : pixels) {
            avg += p;
        }
        avg /= pixels.length;

        long bits = 0L;
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] >= avg) {
                bits |= 1L << (pixels.length - 1 - i);
            }
        }
        return String.format("%016x", bits);
    }

    private int hamming(String a, String b) {
        long v1 = Long.parseUnsignedLong(a, 16);
        long v2 = Long.parseUnsignedLong(b, 16);
        return Long.bitCount(v1 ^ v2);
    }
}
