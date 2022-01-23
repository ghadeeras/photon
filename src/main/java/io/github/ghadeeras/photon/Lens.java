package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

public record Lens(double focalLength) {

    public Focuser focuser(double aperture, double focalDistance) {
        var relativeFocalDistance = focalDistance / focalLength;
        return aperture == 0 ?
            (p, t) -> atFocalLength(p).asRay(t) :
            (p, t) -> randomLensPoint(aperture).towards(atFocalLength(p).scale(relativeFocalDistance), t);
    }

    private Vector atFocalLength(Vector point) {
        return Vector.of(point.x(), point.y(), -focalLength);
    }

    private Vector randomLensPoint(double aperture) {
        return RND.randomVectorInDisk(0, aperture);
    }

    public interface Focuser {

        Ray ray(Vector point, double time);

    }

}
