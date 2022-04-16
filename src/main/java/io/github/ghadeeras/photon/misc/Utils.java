package io.github.ghadeeras.photon.misc;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static io.github.ghadeeras.photon.misc.Constants.epsilon;
import static io.github.ghadeeras.photon.misc.Constants.halfEpsilon;

public class Utils {

    @SuppressWarnings("StatementWithEmptyBody")
    public static int primeJustUnder(int n) {
        var notPrimes = new boolean[n];
        for (int i = 2; i < n; i++) {
            if (notPrimes[i]) {
                continue;
            }
            for (int mi = 2 * i; mi < n; mi += i) {
                notPrimes[mi] = true;
            }
        }
        var p = n;
        while (notPrimes[--p] && p > 3);
        return p;
    }

    public static int staticHashCode() {
        var exception = new Exception();
        return Objects.hash(Stream.concat(
            Stream.of(Thread.currentThread().getName()),
            Arrays.stream(exception.getStackTrace())
                .filter(e -> e.getClassName().startsWith(Utils.class.getPackageName()))
                .skip(1)
        ).toArray(Object[]::new));
    }

    public static boolean approximatelyEqual(double value1, double value2) {
        return Math.abs(value1 - value2) < epsilon;
    }

    public static boolean relativelyEqual(double value1, double value2) {
        return Math.abs(value1 - value2) < Math.abs((value1 + value2) * halfEpsilon);
    }

}
