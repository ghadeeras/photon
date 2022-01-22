package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

import static io.github.ghadeeras.photon.materials.Composite.WeightedMaterial;

public interface Material {

    Effect effectOf(Incident.Hit hit);

    default WeightedMaterial withWeight(double weight) {
        return new WeightedMaterial(this, weight);
    }

}
