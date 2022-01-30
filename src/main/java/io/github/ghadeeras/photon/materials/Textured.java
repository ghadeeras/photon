package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

import java.util.function.Function;

public record Textured(Function<Incident.Hit, Material> texture) implements Material {

    public static Textured with(Function<Incident.Hit, Material> texture) {
        return new Textured(texture);
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        return texture.apply(hit).effectOf(hit);
    }

}
