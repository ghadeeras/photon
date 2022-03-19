package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

import static io.github.ghadeeras.photon.sampling.Samplers.disk;

public record Lens(double focalLength) {

    public Focuser focuser(double aperture, double focalDistance) {
        var relativeFocalDistance = focalDistance / focalLength;
        var lensSurfaceSampler = disk().map(v -> v.scale(aperture)).caching(0x10000);
        return aperture == 0 ?
            (p, t) -> atFocalLength(p).asRay(t) :
            (p, t) -> lensSurfaceSampler.next().towards(atFocalLength(p).scale(relativeFocalDistance), t);
    }

    private Vector atFocalLength(Vector point) {
        return Vector.of(point.x(), point.y(), -focalLength);
    }

    public interface Focuser {

        Ray ray(Vector point, double time);

    }

}
