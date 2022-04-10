package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.structs.Vector;

public class SampleSpaces {

    public static SampleSpace<Vector> square() {
        return SampleSpace.of(Samplers.square(), PDFs.square());
    }

    public static SampleSpace<Vector> disk() {
        return SampleSpace.of(Samplers.disk(), PDFs.disk());
    }

    public static SampleSpace<Vector> sphereSurface() {
        return SampleSpace.of(Samplers.sphereSurface(), PDFs.sphere());
    }

    public static SampleSpace<Vector> sphereSurfacePortion(double cos1, double cos2) {
        return SampleSpace.of(
            Samplers.sphereSurfacePortion(cos1, cos2),
            PDFs.sphereSurfacePortion(cos1, cos2)
        );
    }

    public static SampleSpace<Vector> sphereSurfacePortion(Vector orientation, double cos1, double cos2) {
        return SampleSpace.of(
            Samplers.sphereSurfacePortion(orientation, cos1, cos2),
            PDFs.sphereSurfacePortion(orientation, cos1, cos2)
        );
    }

    public static SampleSpace<Vector> hemisphereSurface() {
        return hemisphereSurface(0);
    }

    public static SampleSpace<Vector> hemisphereSurface(int power) {
        return SampleSpace.of(
            Samplers.hemisphereSurface(power),
            PDFs.hemisphere(power)
        );
    }

    public static SampleSpace<Vector> hemisphereSurface(Vector orientation) {
        return hemisphereSurface(orientation, 0);
    }

    public static SampleSpace<Vector> hemisphereSurface(Vector orientation, int power) {
        return SampleSpace.of(
            Samplers.hemisphereSurface(orientation, power),
            PDFs.hemisphere(orientation, power)
        );
    }

}
