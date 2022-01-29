package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.Function;

public record Textured(Function<Vector, Material> texture) implements Material {

    public static Textured with(Function<Vector, Material> texture) {
        return new Textured(texture);
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        return texture.apply(hit.surfacePosition()).effectOf(hit);
    }

}
