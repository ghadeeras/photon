package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.sampling.SampleSpace;
import io.github.ghadeeras.photon.sampling.SampleSpaces;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Vector;

public record Diffusive(Color color) implements Material {

    public static Diffusive of(Color color) {
        return new Diffusive(color);
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        return Effect.scatteringOf(color, scatteringSpace(hit));
    }

    public static SampleSpace<Vector> scatteringSpace(Incident.Hit hit) {
        return SampleSpaces.hemisphereSurface(hit.point().normal(), 1);
    }

}
