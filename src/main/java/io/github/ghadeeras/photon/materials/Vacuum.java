package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

public class Vacuum implements Material {

    public static final Vacuum instance = new Vacuum();

    @Override
    public Effect effectOf(Incident.Hit hit) {
        return Effect.redirectionOf(Color.colorWhite, hit.ray().direction());
    }

}
