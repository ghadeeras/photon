package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.structs.Matrix;
import io.github.ghadeeras.photon.structs.SurfacePoint;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.Function;

public class Surfaces {

    public static Surface surface(SampleSpace<Vector> space, Function<Vector, SurfacePoint> surfacePointFunction) {
        return Surface.of(space.map(surfacePointFunction), p -> space.pdf(p.position()));
    }

    public static Surface square() {
        return surface(SampleSpaces.square(), pointOnXYPlane());
    }

    public static Surface disk() {
        return surface(SampleSpaces.disk(), pointOnXYPlane());
    }

    public static Surface sphereSurface() {
        return surface(SampleSpaces.sphereSurface(), pointOnUnitSphere());
    }

    public static Surface sphereSurfacePortion(double cos1, double cos2) {
        return surface(SampleSpaces.sphereSurfacePortion(cos1, cos2), pointOnUnitSphere());
    }

    public static Surface sphereSurfacePortion(Vector orientation, double cos1, double cos2) {
        return sphereSurfacePortion(cos1, cos2).transform(Matrix.yAlignedWith(orientation));
    }

    public static Surface hemisphereSurface() {
        return hemisphereSurface(0);
    }

    public static Surface hemisphereSurface(int power) {
        return surface(SampleSpaces.hemisphereSurface(power), pointOnUnitSphere());
    }

    public static Surface hemisphereSurface(Vector orientation) {
        return hemisphereSurface(orientation, 0);
    }

    public static Surface hemisphereSurface(Vector orientation, int power) {
        return hemisphereSurface(power).transform(Matrix.yAlignedWith(orientation));
    }

    private static Function<Vector, SurfacePoint> pointOnXYPlane() {
        return v -> SurfacePoint.of(v, Vector.unitZ);
    }

    private static Function<Vector, SurfacePoint> pointOnUnitSphere() {
        return v -> SurfacePoint.of(v, v);
    }

}
