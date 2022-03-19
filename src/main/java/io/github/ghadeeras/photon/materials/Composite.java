package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.Sampler;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

import static io.github.ghadeeras.photon.sampling.Samplers.unsigned;

public record Composite(Sampler<Material> materialsSampler) implements Material {

    public static Composite of(WeightedMaterial... materials) {
        var weightSum = weightSum(materials);
        var normalizedMaterials = weightSum == 1 ? materials : normalizeWeights(materials, weightSum);
        return new Composite(unsigned().map(choice -> selectedMaterial(choice, normalizedMaterials)).caching(0x100 * materials.length));
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

    private static Material selectedMaterial(Double choice, WeightedMaterial[] materials) {
        Material selectedMaterial = Emissive.of(Color.colorBlack);
        for (var material : materials) {
            choice -= material.weight;
            if (choice < 0) {
                selectedMaterial = material;
                break;
            }
        }
        return selectedMaterial;
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        return materialsSampler.next().effectOf(hit);
    }

    public record WeightedMaterial(Material material, double weight) implements Material {

        @Override
        public Effect effectOf(Incident.Hit hit) {
            return material.effectOf(hit);
        }

    }

}
