package io.github.ghadeeras.photon.structs;

import io.github.ghadeeras.photon.RND;

public record Color(double red, double green, double blue) {

    public static Color colorBlack = whiteShade(0);
    public static Color colorWhite = whiteShade(1);
    public static Color colorRed = redShade(1);
    public static Color colorGreen = greenShade(1);
    public static Color colorBlue = blueShade(1);

    public static Color whiteShade(double intensity) {
        return of(intensity, intensity, intensity);
    }

    public static Color redShade(double intensity) {
        return of(intensity, 0, 0);
    }

    public static Color greenShade(double intensity) {
        return of(0, intensity, 0);
    }

    public static Color blueShade(double intensity) {
        return of(0, 0, intensity);
    }

    public static Color of(double red, double green, double blue) {
        return new Color(red, green, blue);
    }

    public Color scale(double factor) {
        return of(red * factor, green * factor, blue * factor);
    }

    public Color plus(Color that) {
        return of(this.red + that.red, this.green + that.green, this.blue + that.blue);
    }

    public Color mul(Color that) {
        return of(this.red * that.red, this.green * that.green, this.blue * that.blue);
    }

    public Color gammaCorrected() {
        return of(gammaCorrect(red), gammaCorrect(green), gammaCorrect(blue));
    }

    public Color dithered() {
        return of(dither(red), dither(green), dither(blue));
    }

    public Color clamped() {
        return of(clamp(red), clamp(green), clamp(blue));
    }

    private static double gammaCorrect(double component) {
        return Math.sqrt(clamp(component));
    }

    private static double dither(double component) {
        return clamp(component + RND.anySigned() / 512);
    }

    private static double clamp(double component) {
        return Math.min(Math.max(component, 0), 1);
    }

    public double red() {
        return red;
    }

    public double green() {
        return green;
    }

    public double blue() {
        return blue;
    }

    public double[] components() {
        return new double[] { red, green, blue };
    }

}
