package io.github.ghadeeras.photon.noise;

import io.github.ghadeeras.photon.structs.Vector;

import static io.github.ghadeeras.photon.sampling.Samplers.shuffledIndexes;
import static io.github.ghadeeras.photon.sampling.Samplers.sphereSurface;

public class Perlin implements Noise {

    private static final int count = 1 << 8;
    private static final int mask = count - 1;

    private final Vector[] randomVectors;

    private final int[] xIndices;
    private final int[] yIndices;
    private final int[] zIndices;

    public Perlin() {
        var indices = shuffledIndexes(count).next(3);
        randomVectors = sphereSurface().next(count).toArray(Vector[]::new);
        xIndices = indices.get(0);
        yIndices = indices.get(1);
        zIndices = indices.get(2);
    }

    @Override
    public double noise(Vector point) {
        var x = Math.floor(point.x());
        var y = Math.floor(point.y());
        var z = Math.floor(point.z());

        var dX = point.x() - x;
        var dY = point.y() - y;
        var dZ = point.z() - z;

        var iX = (int) x;
        var iY = (int) y;
        var iZ = (int) z;

        var wX = hermiteCubic(dX);
        var wY = hermiteCubic(dY);
        var wZ = hermiteCubic(dZ);

        var result = 0D;
        for (int i = 0; i < 2; i++) {
            var wI = i == 0 ? 1 - wX : wX;
            for (int j = 0; j < 2; j++) {
                var wJ = j == 0 ? 1 - wY : wY;
                for (int k = 0; k < 2; k++) {
                    var wK = k == 0 ? 1 - wZ : wZ;
                    result += wI * wJ * wK * noise(iX, iY, iZ, dX, dY, dZ, i, j, k);
                }
            }
        }
        return result;
    }

    private double hermiteCubic(double value) {
        return value * value * (3 - 2 * value);
    }

    private double noise(int x, int y, int z, double dX, double dY, double dZ, int i, int j, int k) {
        return Vector.of(dX - i, dY - j, dZ - k).dot(vector(x + i, y + j, z + k));
    }

    private Vector vector(int x, int y, int z) {
        int i = xIndices[x & mask] ^ yIndices[y & mask] ^ zIndices[z & mask];
        return randomVectors[i & mask];
    }

}
