package com.example.facebook2pastvu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class ImageMatcherTest {
    @Test
    public void testHash() throws Exception {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                img.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        Path tmp = Files.createTempFile("img", ".png");
        ImageIO.write(img, "png", tmp.toFile());

        ImageMatcher matcher = new ImageMatcher();
        String hash = matcher.hash(tmp);
        assertNotNull(hash);
        Files.deleteIfExists(tmp);
    }
}
