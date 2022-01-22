package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.Function;

public class Lens {

    public final double focalLength;
    public final double focalDistance;
    public final double aperture;

    private final Function<Vector, Ray> ray;

    public Lens(double focalLength, double focalDistance, double aperture) {
        this.aperture = aperture;
        this.focalDistance = focalDistance;
        this.focalLength = focalLength;

        var relativeFocalDistance = focalDistance / focalLength;
        this.ray = aperture == 0 ?
            Vector::asRay :
            p -> defocusedRay(p, relativeFocalDistance);
    }

    private Ray defocusedRay(Vector point, double relativeFocalDistance) {
        return RND.randomVectorInDisk(0, aperture).towards(point.scale(relativeFocalDistance));
    }

    public Ray ray(Vector point) {
        return ray.apply(Vector.of(point.x(), point.y(), -focalLength));
    }

}
