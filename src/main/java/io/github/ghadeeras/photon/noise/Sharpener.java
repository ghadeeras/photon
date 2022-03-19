package io.github.ghadeeras.photon.noise;

import io.github.ghadeeras.photon.Noise;
import io.github.ghadeeras.photon.structs.Vector;

public record Sharpener(Noise noise, int depth) implements Noise {

    @Override
    public double noise(Vector point) {
        var result = 0D;
        var p = point;
        var w = 1D;
        for (int i = 0; i < depth; i++) {
            result += w * noise.noise(p);
            p = p.scale(2);
            w /= 2;
        }
        return result;
    }

}
