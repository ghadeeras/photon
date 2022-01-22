package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.RND;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

public record Reflective(Color color, double fuzziness) implements Material {

    public static Reflective of(Color color) {
        return of(color, 0);
    }

    public static Reflective of(Color color, double fuzziness) {
        return new Reflective(color, fuzziness);
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        var normal = fuzziness != 0 ?
            hit.normal().plus(RND.randomVectorInSphere(0, fuzziness)).unit():
            hit.normal();
        var incident = hit.ray().direction();
        var reflection = incident.minus(normal.scale(2 * normal.dot(incident)));
        return Effect.redirectionOf(color, reflection);
    }

}
