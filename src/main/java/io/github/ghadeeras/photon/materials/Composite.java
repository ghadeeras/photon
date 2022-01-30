package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.RND;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

public record Composite(WeightedMaterial... materials) implements Material {

    public Composite(WeightedMaterial... materials) {
        var weightSum = weightSum(materials);
        this.materials = weightSum == 1 ? materials : normalizeWeights(materials, weightSum);
    }

    private static double weightSum(WeightedMaterial[] materials) {
        var sum = 0D ;
        for (var material : materials) {
            sum += material.weight;
        }
        return sum;
    }

    private static WeightedMaterial[] normalizeWeights(WeightedMaterial[] materials, double weightSum) {
        var result = new WeightedMaterial[materials.length];
        for (int i = 0; i < materials.length; i++) {
            var material = materials[i];
            result[i] = material.withWeight(material.weight / weightSum);
        }
        return result;
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
