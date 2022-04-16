package io.github.ghadeeras.photon.structs;

public record SurfacePoint(Vector position, Vector sampleArea) {

    public static SurfacePoint of(Vector position, Vector sampleArea) {
        return new SurfacePoint(position, sampleArea);
    }

}
