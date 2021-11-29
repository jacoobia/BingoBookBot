package com.jacoobia.bingobookbot.utils;

import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageUtils {

    public static BufferedImage loadImage(String url, boolean local) {
        try {
            if(local) {
                File file = ResourceUtils.getFile(url);
                return ImageIO.read(file);
            } else {
                URL imageUrl = new URL(url);
                return ImageIO.read(imageUrl.openStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}