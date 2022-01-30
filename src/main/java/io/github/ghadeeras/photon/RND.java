package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Vector;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class RND {

    public static double anyUnsigned() {
        return anyUnsigned(1);
    }

    public static double anySigned() {
        return anySigned(1);
    }

    public static double anyUnsigned(double range) {
        return any(0, range);
    }

    public static double anySigned(double range) {
        return any(-range, range);
    }

    public static double any(double origin, double bound) {
        return ThreadLocalRandom.current().nextDouble(origin, bound);
    }

    public static Vector randomUnitVector() {
        return randomVectorInSphere(0.001, 1).unit();
    }

    public static Vector randomVectorInSphere(double minRadius, double maxRadius) {
        var minRadiusSquared = minRadius * minRadius;
        var maxRadiusSquared = maxRadius * maxRadius;
        while (true) {
            var x = RND.anySigned(maxRadius);
            var y = RND.anySigned(maxRadius);
            var z = RND.anySigned(maxRadius);
            var l2 = x * x + y * y + z * z;
            if (l2 > minRadiusSquared && l2 < maxRadiusSquared) {
                return Vector.of(x, y, z);
            }
        }
    }

    public static Vector randomVectorInDisk(double minRadius, double maxRadius) {
        var minRadiusSquared = minRadius * minRadius;
        var maxRadiusSquared = maxRadius * maxRadius;
        while (true) {
            var x = RND.anySigned(maxRadius);
            var y = RND.anySigned(maxRadius);
            var l2 = x * x + y * y;
            if (l2 > minRadiusSquared && l2 < maxRadiusSquared) {
                return Vector.of(x, y, 0);
            }
        }
    }

    public static int[] randomIndices(int count) {
        var result = IntStream.range(0, count).toArray();
        for (int i = count - 1; i > 0; i--) {
            int j = ThreadLocalRandom.current().nextInt(i);
            int n = result[i];
            result[i] = result[j];
            result[j] = n;
        }
        return result;
    }

}
