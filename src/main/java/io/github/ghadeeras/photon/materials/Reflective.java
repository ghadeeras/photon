package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.sampling.Sampler;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Vector;

import static io.github.ghadeeras.photon.sampling.Samplers.sphere;

public record Reflective(Color color, Sampler<Vector> fuzzinessSampler) implements Material {

    public static Reflective of(Color color) {
        return of(color, 0);
    }

    public static Reflective of(Color color, double fuzziness) {
        return new Reflective(color, fuzziness != 0 ? sphere().map(v -> v.scale(fuzziness)).caching(0x10000) : null);
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        var normal = fuzzinessSampler != null ?
            hit.point().sampleArea().unit().plus(fuzzinessSampler.next()).unit() :
            hit.point().sampleArea().unit();
        var incident = hit.ray().direction();
        var reflection = incident.minus(normal.scale(2 * normal.dot(incident)));
        return Effect.redirectionOf(color, reflection);
    }

}
