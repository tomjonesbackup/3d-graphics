package com.twj.gfx;

import java.awt.Color;

public class ColourUtils {

    static int getColorRGB(int rgb, double brightness) {
        Color base = new Color(rgb);
        int red = Math.max(Math.min(255, (int) (base.getRed() + (base.getRed() * (1.5 * brightness)))), 0);
        int green = Math.max(Math.min(255, (int) (base.getGreen() + (base.getGreen() * (brightness)))), 0);
        int blue = Math.max(Math.min(255, (int) (base.getBlue() + (base.getBlue() * (brightness)))), 0);
        return new Color(red, green, blue).getRGB();
    }
}
