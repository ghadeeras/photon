package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.RND;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

import java.util.stream.Stream;

public record Composite(WeightedMaterial... materials) implements Material {

    public Composite(WeightedMaterial... materials) {
        var weightSum = Stream.of(materials)
            .map(WeightedMaterial::weight)
            .reduce(0.0, Double::sum);
        this.materials = Stream.of(materials)
            .map(material -> material.material().withWeight(material.weight / weightSum))
            .toArray(WeightedMaterial[]::new);
    }

    public static Composite of(WeightedMaterial... materials) {
        return new Composite(materials);
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        var choice = RND.anyUnsigned();
        for (var material : materials) {
            choice -= material.weight;
            if (choice < 0) {
                return material.effectOf(hit);
            }
        }
        return Effect.emissionOf(Color.colorBlack);
    }

    public static record WeightedMaterial(Material material, double weight) implements Material {

        @Override
        public Effect effectOf(Incident.Hit hit) {
            return material.effectOf(hit);
        }

    }

}
