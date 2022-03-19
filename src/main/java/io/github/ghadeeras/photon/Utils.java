package io.github.ghadeeras.photon;

import java.util.Arrays;
import java.util.Objects;

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
        return Objects.hash(Arrays.stream(exception.getStackTrace())
            .filter(e -> e.getClassName().startsWith(Utils.class.getPackageName()))
            .skip(1)
            .toArray(Object[]::new)
        );
    }

}
