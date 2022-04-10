package io.github.ghadeeras.photon;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class Utils {

    public static double epsilon = 0x0.002P-7;
    public static double halfEpsilon = epsilon / 2;

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
